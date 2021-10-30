package de.invesdwin.context.integration.marshaller;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * https://stackoverflow.com/questions/9895041/merging-two-json-documents-using-jackson
 * 
 */
@Immutable
public final class JsonMerge {

    private JsonMerge() {}

    /**
     * Merge two JSON tree into one i.e mergedInTo.
     *
     * @param toBeMerged
     * @param mergedInTo
     */
    public static void merge(final JsonNode toBeMerged, final JsonNode mergedInTo) {
        final Iterator<Map.Entry<String, JsonNode>> incomingFieldsIterator = toBeMerged.fields();
        Iterator<Map.Entry<String, JsonNode>> mergedIterator = mergedInTo.fields();

        while (incomingFieldsIterator.hasNext()) {
            final Map.Entry<String, JsonNode> incomingEntry = incomingFieldsIterator.next();

            final JsonNode subNode = incomingEntry.getValue();

            if (subNode.getNodeType().equals(JsonNodeType.OBJECT)) {
                boolean isNewBlock = true;
                mergedIterator = mergedInTo.fields();
                while (mergedIterator.hasNext()) {
                    final Map.Entry<String, JsonNode> entry = mergedIterator.next();
                    if (entry.getKey().equals(incomingEntry.getKey())) {
                        merge(incomingEntry.getValue(), entry.getValue());
                        isNewBlock = false;
                    }
                }
                if (isNewBlock) {
                    ((ObjectNode) mergedInTo).replace(incomingEntry.getKey(), incomingEntry.getValue());
                }
            } else if (subNode.getNodeType().equals(JsonNodeType.ARRAY)) {
                boolean newEntry = true;
                mergedIterator = mergedInTo.fields();
                while (mergedIterator.hasNext()) {
                    final Map.Entry<String, JsonNode> entry = mergedIterator.next();
                    if (entry.getKey().equals(incomingEntry.getKey())) {
                        updateArray(incomingEntry.getValue(), entry);
                        newEntry = false;
                    }
                }
                if (newEntry) {
                    ((ObjectNode) mergedInTo).replace(incomingEntry.getKey(), incomingEntry.getValue());
                }
            }
            ValueNode valueNode = null;
            final JsonNode incomingValueNode = incomingEntry.getValue();
            switch (subNode.getNodeType()) {
            case STRING:
                valueNode = new TextNode(incomingValueNode.textValue());
                break;
            case NUMBER:
                valueNode = new IntNode(incomingValueNode.intValue());
                break;
            case BOOLEAN:
                valueNode = BooleanNode.valueOf(incomingValueNode.booleanValue());
            default:
                //ignore
            }
            if (valueNode != null) {
                updateObject(mergedInTo, valueNode, incomingEntry);
            }
        }
    }

    private static void updateArray(final JsonNode valueToBePlaced, final Map.Entry<String, JsonNode> toBeMerged) {
        final JsonNode valueToBeMerged = toBeMerged.getValue();
        if (valueToBeMerged instanceof ArrayNode && valueToBePlaced instanceof ArrayNode) {
            final ArrayNode valueToBeMergedArrayNode = (ArrayNode) valueToBeMerged;
            final ArrayNode valueToBePlacedArrayNode = (ArrayNode) valueToBePlaced;
            valueToBeMergedArrayNode.addAll(valueToBePlacedArrayNode);
        } else {
            toBeMerged.setValue(valueToBePlaced);
        }
    }

    private static void updateObject(final JsonNode mergeInTo, final ValueNode valueToBePlaced,
            final Map.Entry<String, JsonNode> toBeMerged) {
        boolean newEntry = true;
        final Iterator<Map.Entry<String, JsonNode>> mergedIterator = mergeInTo.fields();
        while (mergedIterator.hasNext()) {
            final Map.Entry<String, JsonNode> entry = mergedIterator.next();
            if (entry.getKey().equals(toBeMerged.getKey())) {
                newEntry = false;
                entry.setValue(valueToBePlaced);
            }
        }
        if (newEntry) {
            ((ObjectNode) mergeInTo).replace(toBeMerged.getKey(), toBeMerged.getValue());
        }
    }

}
