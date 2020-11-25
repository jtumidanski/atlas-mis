package com.atlas.mis.attribute;

import java.util.List;

import rest.AttributeResult;

public record MapAttributes(String name, String streetName, Integer returnMapId, Float monsterRate,
                            String onFirstUserEnter, String onUserEnter, Integer fieldLimit, Short mobInterval, Integer seats,
                            Boolean clock, Boolean everLast, Boolean town, Integer decHp, Integer protectItem,
                            Integer forcedReturnMap, Boolean boat, Integer timeLimit, Integer fieldType, Integer mobCapacity,
                            Float recovery, RectangleAttributes mapArea,
                            List<RectangleAttributes> area,
                            List<BackgroundTypeAttributes> backgroundTypes
) implements AttributeResult {
}
