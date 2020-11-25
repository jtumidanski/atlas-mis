package com.atlas.mis.attribute;

import rest.AttributeResult;

public record RectangleAttributes(Integer x, Integer y, Integer width, Integer height) implements AttributeResult {
}
