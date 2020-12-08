package com.atlas.mis.model;

import java.awt.*;
import java.util.List;

public record MapData(int id, String name, String streetName, int returnMapId, float monsterRate, String onFirstUserEnter,
                      String onUserEnter, int fieldLimit, short mobInterval, List<PortalData> portals, TimeMob timeMob,
                      Rectangle mapArea, FootholdTree footholdTree, List<Rectangle> areas, int seats, boolean clock,
                      boolean everLast, boolean town, int decHp, int protectItem, int forcedReturnMap, boolean boat,
                      int timeLimit, int fieldType, int mobCapacity, float recovery, List<BackgroundType> backgroundTypes,
                      XLimit xLimit, List<Reactor> reactors, List<Npc> npcs, List<Monster> monsters) {
}
