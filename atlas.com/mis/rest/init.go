package rest

import (
	_map "atlas-mis/map"
	"atlas-mis/monster"
	"context"
	"github.com/gorilla/mux"
	"github.com/sirupsen/logrus"
	"net/http"
	"sync"
)

func CreateRestService(l *logrus.Logger, ctx context.Context, wg *sync.WaitGroup) {
	go NewServer(l, ctx, wg, ProduceRoutes())
}

func ProduceRoutes() func(l logrus.FieldLogger) http.Handler {
	return func(l logrus.FieldLogger) http.Handler {
		router := mux.NewRouter().PathPrefix("/ms/mis").Subrouter().StrictSlash(true)
		router.Use(CommonHeader)

		monr := router.PathPrefix("/monsters").Subrouter()
		monr.HandleFunc("/{monsterId}", monster.HandleGetMonsterRequest(l)).Methods(http.MethodGet)
		monr.HandleFunc("/{monsterId}/loseItems", monster.HandleGetMonsterLoseItemsRequest(l)).Methods(http.MethodGet)

		mapr := router.PathPrefix("/maps").Subrouter()
		mapr.HandleFunc("/{mapId}", _map.HandleGetMapRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/portals", _map.HandleGetMapPortalsByNameRequest(l)).Queries("name", "{name}").Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/portals", _map.HandleGetMapPortalsRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/portals/{portalId}", _map.HandleGetMapPortalRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/reactors", _map.HandleGetMapReactorsRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/npcs", _map.HandleGetMapNPCsByObjectIdRequest(l)).Queries("objectId", "{objectId}").Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/npcs", _map.HandleGetMapNPCsRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/npcs/{npcId}", _map.HandleGetMapNPCRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/monsters", _map.HandleGetMapMonstersRequest(l)).Methods(http.MethodGet)
		mapr.HandleFunc("/{mapId}/dropPosition", _map.HandleGetMapDropPositionRequest(l)).Methods(http.MethodPost)


		return router
	}
}
