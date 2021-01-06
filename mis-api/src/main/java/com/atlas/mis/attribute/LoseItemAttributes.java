package com.atlas.mis.attribute;

import rest.AttributeResult;

public record LoseItemAttributes(Byte chance, Byte x) implements AttributeResult {
}
