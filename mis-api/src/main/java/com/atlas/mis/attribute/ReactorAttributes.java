package com.atlas.mis.attribute;

import rest.AttributeResult;

public record ReactorAttributes(String name, Integer x, Integer y, Integer delay, Byte facingDirection) implements AttributeResult {
}
