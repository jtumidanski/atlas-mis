package com.atlas.mis.attribute;

import rest.AttributeResult;

public record MonsterAttributes(Integer monsterId, Integer mobTime, Integer team, Integer cy, Integer f, Integer fh, Integer rx0,
                                Integer rx1, Integer x, Integer y, Boolean hide) implements AttributeResult {
}
