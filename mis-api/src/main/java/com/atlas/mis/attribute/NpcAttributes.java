package com.atlas.mis.attribute;

import rest.AttributeResult;

public record NpcAttributes(Integer id, String name, Integer cy, Integer f, Integer fh, Integer rx0, Integer rx1, Integer x,
                            Integer y, Boolean hide) implements AttributeResult {
}
