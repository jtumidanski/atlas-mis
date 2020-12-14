package com.atlas.mis.attribute;

import rest.AttributeResult;

public record DropPositionInputAttributes(Integer initialX, Integer initialY, Integer fallbackX, Integer fallbackY)
      implements AttributeResult {
}
