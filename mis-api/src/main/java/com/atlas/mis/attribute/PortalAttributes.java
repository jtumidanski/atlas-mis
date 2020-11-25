package com.atlas.mis.attribute;

import rest.AttributeResult;

public record PortalAttributes(String name, String target, Integer type, Integer x, Integer y, Integer targetMap,
                               String scriptName) implements AttributeResult {
}
