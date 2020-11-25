package com.atlas.mis.model;

import java.awt.*;

public record PortalData(int id, String name, String target, int type, Point position, int targetMap, String scriptName) {
}
