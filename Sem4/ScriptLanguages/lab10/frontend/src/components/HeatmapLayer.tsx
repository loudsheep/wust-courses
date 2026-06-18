import { Polyline } from "react-leaflet";
import { useEffect, useState } from "react";
import { fetchHeatmap, type HeatmapEdge } from "@/lib/api";
import { heatColor, heatWeight } from "@/lib/heatColor";

export function HeatmapLayer() {
  const [edges, setEdges] = useState<HeatmapEdge[]>([]);

  useEffect(() => {
    fetchHeatmap().then(setEdges).catch(console.error);
  }, []);

  if (edges.length === 0) return null;

  const maxFrequency = Math.max(...edges.map((edge) => edge.frequency));

  return (
    <>
      {[...edges]
        .sort((a, b) => a.frequency - b.frequency)
        .map((edge, index) => {
          const intensity = maxFrequency > 0 ? edge.frequency / maxFrequency : 0;

          return (
            <Polyline
              key={`${edge.from_stop_id}-${edge.to_stop_id}-${index}`}
              positions={[
                [edge.from_lat, edge.from_lon],
                [edge.to_lat, edge.to_lon],
              ]}
              pathOptions={{
                color: heatColor(intensity),
                weight: heatWeight(intensity),
              }}
            />
          );
        })}
    </>
  );
}
