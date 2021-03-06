package uk.ac.susx.shl.micromacro.core.data.text;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.susx.tag.method51.core.meta.Datum;
import uk.ac.susx.tag.method51.core.meta.Key;
import uk.ac.susx.tag.method51.core.meta.KeySet;
import uk.ac.susx.tag.method51.core.meta.span.Spans;
import uk.ac.susx.tag.method51.core.meta.types.RuntimeType;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by sw206 on 09/05/2018.
 */
public class XML2Datum extends DefaultHandler {

    public static class Element {

        private final Element link;

        private final String name;

        private final ImmutableMap<String, String> attributes;

        private final boolean isContainer;

        private final boolean selfClosing;

        private final String label;

        private final String valueAttribute;

        private Element(String name, Attributes attributes) {
            this.name = name;
            Map<String, String> attrs = new HashMap<>();
            for(int i = 0; i < attributes.getLength(); ++i) {
                String k = attributes.getQName(i);
                String v = attributes.getValue(i);
                attrs.put(k,v);
            }
            this.attributes = ImmutableMap.copyOf(attrs);
            label = null;
            valueAttribute = null;
            isContainer = false;
            selfClosing = false;
            link = null;
        }

        public Element(String name, Map<String, String> attributes, String label) {
            this(name, attributes, label, null, false, false, null);
        }



        private Element(String name, Map<String, String> attributes, String label, String valueAttribute, boolean selfClosing, boolean isContainer, Element link) {
            this.name = name;
            this.attributes = ImmutableMap.copyOf(attributes);
            this.label = label;
            this.valueAttribute = valueAttribute;
            this.selfClosing = selfClosing;
            this.isContainer = isContainer;
            this.link = link;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Element element = (Element) o;
            return com.google.common.base.Objects.equal(name, element.name) &&
                    com.google.common.base.Objects.equal(attributes, element.attributes);
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(name, attributes);
        }

        private boolean is(Element other) {
            return other.name.equals(name) && other.attributes.entrySet().containsAll(attributes.entrySet());
        }

        private boolean selfClosing() {
            return selfClosing;
        }

        public Element attributes(Map<String, String> attributes) {
            return new Element(name, attributes, label, valueAttribute, selfClosing, isContainer, link);
        }

        public Element valueAttribute(String valueAttribute) {
            return new Element(name, attributes, label, valueAttribute, selfClosing, isContainer, link);
        }

        public Element selfClosing(boolean selfClosing) {
            return new Element(name, attributes, label, valueAttribute, selfClosing, isContainer, link);
        }

        public Element link(Element link) {
            return new Element(name, attributes, label, valueAttribute, selfClosing, isContainer, link);
        }

        public Element isContainer(boolean isContainer) {
            return new Element(name, attributes, label, valueAttribute, selfClosing, isContainer, link);
        }
    }

    private ArrayDeque<AbstractMap.SimpleImmutableEntry<Integer,Optional<Element>>> stack;

    private Datum datum;

    private StringBuilder text;

    private final Map<Element, Key<Spans<String, String>>> interestingElements;

    private int i;

    private boolean enabled;

    private final Key<String> textKey;

    private final KeySet keys;

    public XML2Datum(Map<Key<Spans<String, String>>, List<Element>> interestingElements) {
        i = 0;
        enabled = false;
        stack = new ArrayDeque<>();
        datum = new Datum();
        text = new StringBuilder();

        textKey = Key.of("text", RuntimeType.STRING);

        Map<Element, Key<Spans<String, String>>> ies = new HashMap<>();

        for(Map.Entry<Key<Spans<String, String>>, List<Element>> entry : interestingElements.entrySet()) {
            for(Element element : entry.getValue()) {
                ies.put(element, entry.getKey());
            }
        }

        this.interestingElements = ImmutableMap.copyOf(ies);

//        keys = getKeys(interestingElements);
        keys = KeySet.ofIterable(new HashSet<>(this.interestingElements.values()));

    }

    public static KeySet getKeys(List<Element> interestingElements) {
        KeySet ks = KeySet.of();

        for(Element e : interestingElements) {
            ks = ks.with(Key.of(e.label, RuntimeType.stringSpans(String.class)));
        }

        return ks;
    }

    public KeySet getKeys() {
        return keys;
    }

    public Key<String> getTextKey() {
        return textKey;
    }

    private void startCheck(Element element) {
        if(element.isContainer) {
            enabled = true;
        }
    }

    private void endCheck(Element element) {
        if(element.isContainer) {
            datum = datum.with(textKey, text.toString());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        Element element = new Element(qName, attributes);

        Optional<Element> possiblyInteresting = Optional.empty();

        Set<Element> ies = interestingElements.keySet();

        for(Element interesting : ies) {
            if(interesting.is(element)) {
                if(possiblyInteresting.isPresent()) {
                    throw new RuntimeException("Element already matched.");
                }
                possiblyInteresting = Optional.of(interesting.attributes(element.attributes).link(interesting));
                startCheck(possiblyInteresting.get());
            }
        }

        stack.push(new AbstractMap.SimpleImmutableEntry<>(i, possiblyInteresting));

//        System.out.println("push " + qName);
    }


    private int getStart() {
        Iterator<AbstractMap.SimpleImmutableEntry<Integer, Optional<Element>>> itr = stack.iterator();
        while(itr.hasNext() ) {
            AbstractMap.SimpleImmutableEntry<Integer, Optional<Element>> entry = itr.next();
            if(entry.getValue().isPresent() && !entry.getValue().get().selfClosing()) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("No non-self closing parent.");
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        AbstractMap.SimpleImmutableEntry<Integer, Optional<Element>> entry = stack.pop();

        if(entry.getValue().isPresent()) {

            Element element = entry.getValue().get();
            int start;
            if(element.selfClosing()) {
                start = getStart();
            } else {
                start = entry.getKey();
            }
            int end = i;
            String label = element.label;
            Key<Spans<String, String>> key = interestingElements.get(element.link);
//            Key<Spans<String, String>> key = Key.of(label, RuntimeType.stringSpans(String.class));

            Spans<String, String> spans;

            if(datum.get().containsKey(key)) {
                spans = datum.get(key);
            } else {
                spans = Spans.annotate(textKey, String.class);
            }

            String value = label;
            if(element.valueAttribute !=null) {
                value = element.attributes.get(element.valueAttribute);
            }

//            String span = text.toString().substring(start, end);

            datum = datum.with(key, spans.with(start, end, value));

            endCheck(element);
        }

//        System.out.println("pop " + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(enabled) {
            String dirty = new String(ch, start, length);
            String clean = clean(dirty);
            text.append(clean);
            i += clean.length();
        }
    }

    private String clean(String dirty) {
        dirty = " " + dirty + " ";
        String cleaner = dirty.replaceAll("\\s+", " ");
        return cleaner.equals(" ") ? "" : cleaner;
    }

    public Datum getDatum() {
        return datum;
    }


    public static Iterable<Datum> getData(Path start, Set<Path> files, Map<Key<Spans<String, String>>, List<Element>> interestingElements, String documentNode, String suffix)  {

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();

            SAXParser saxParser = factory.newSAXParser();

            Deque<Path> paths = new ArrayDeque<>(Files.walk(start).filter(path -> ( files.contains(path) || files.isEmpty() ) && path.toString().endsWith("xml")).collect(Collectors.toList()));

            return new Iterable<Datum>() {
                @Override
                public Iterator<Datum> iterator() {
                    return new Iterator<Datum>() {
                        Deque<Datum> buffer = new ArrayDeque<>();

                        @Override
                        public boolean hasNext() {
                            if(buffer.isEmpty() && !paths.isEmpty()) {
                                try {
                                    Path path = paths.pop();
                                    XML2Datum handler = new XML2Datum(interestingElements);
                                    InputStream xmlInput = Files.newInputStream(path);
                                    saxParser.parse(xmlInput, handler);
                                    Datum datum = handler.getDatum();
                                    KeySet keys = handler.getKeys();
                                    Key<Spans<String, String>> documentKey = keys.get(documentNode);

                                    for (Datum doc : datum.getSpannedData(documentKey, keys, suffix)) {
                                        buffer.add(doc);
                                    }

                                    if(buffer.isEmpty()) {
                                        return hasNext();
                                    } else {
                                        return true;
                                    }

                                } catch (IOException | SAXException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if(!buffer.isEmpty()) {
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public Datum next() {
                            return buffer.pop();
                        }
                    };
                }
            };

        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
