package monster

type DataListContainer struct {
	Data []DataBody `json:"data"`
}

type DataBody struct {
	Id         string     `json:"id"`
	Type       string     `json:"type"`
	Attributes Attributes `json:"attributes"`
}

type Attributes struct {
	Id      uint32 `json:"id"`
	MobTime uint32 `json:"mob_time"`
	Team    int32  `json:"team"`
	CY      int16  `json:"cy"`
	F       uint32 `json:"f"`
	FH      uint16 `json:"fh"`
	RX0     int16  `json:"rx0"`
	RX1     int16  `json:"rx1"`
	X       int16  `json:"x"`
	Y       int16  `json:"y"`
	Hide    bool   `json:"hide"`
}
