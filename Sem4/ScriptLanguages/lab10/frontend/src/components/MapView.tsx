import { MapContainer, TileLayer } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { BusStopMarker, type StopMarkerData } from "@/components/BusStopMarker";
import { HeatmapLayer } from "@/components/HeatmapLayer";
import { MapControls } from "@/components/MapControls";
import { useEffect, useState } from "react";

const WROCLAW_CENTER: [number, number] = [51.1079, 17.0385];

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8000";

export function MapView() {
  const [stops, setStops] = useState<StopMarkerData[]>([]);
  const [loading, setLoading] = useState(true);
  const [showStops, setShowStops] = useState(true);
  const [showHeatmap, setShowHeatmap] = useState(false);

  useEffect(() => {
    fetch(`${API_URL}/stops`)
      .then((res) => res.json())
      .then(setStops)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="relative h-full w-full">
      <MapContainer
        center={WROCLAW_CENTER}
        zoom={13}
        className="h-full w-full"
        zoomControl
      >
        <TileLayer
          attribution="&copy; OpenStreetMap contributors & CARTO"
          url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
          subdomains={["a", "b", "c", "d"]}
        />

        {loading && (
          <div className="absolute z-[1000] text-white">Loading stops...</div>
        )}

        {showHeatmap && <HeatmapLayer />}

        {showStops &&
          stops.map((stop) => <BusStopMarker key={stop.stopId} stop={stop} />)}
      </MapContainer>

      <MapControls
        showStops={showStops}
        onShowStopsChange={setShowStops}
        showHeatmap={showHeatmap}
        onShowHeatmapChange={setShowHeatmap}
      />
    </div>
  );
}
