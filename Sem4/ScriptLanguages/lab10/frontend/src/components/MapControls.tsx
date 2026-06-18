interface MapControlsProps {
  showStops: boolean;
  onShowStopsChange: (value: boolean) => void;
  showHeatmap: boolean;
  onShowHeatmapChange: (value: boolean) => void;
}

export function MapControls({
  showStops,
  onShowStopsChange,
  showHeatmap,
  onShowHeatmapChange,
}: MapControlsProps) {
  return (
    <div className="pointer-events-none absolute bottom-4 left-4 z-[1000]">
      <div className="pointer-events-auto flex flex-col gap-3 rounded-lg border border-white/10 bg-black/60 px-4 py-3 backdrop-blur-sm">
        <h2 className="text-sm font-semibold text-white">Warstwy mapy</h2>

        <label className="flex items-center gap-2 text-sm text-gray-300">
          <input
            type="checkbox"
            checked={showStops}
            onChange={(e) => onShowStopsChange(e.target.checked)}
          />
          Przystanki
        </label>

        <label className="flex items-center gap-2 text-sm text-gray-300">
          <input
            type="checkbox"
            checked={showHeatmap}
            onChange={(e) => onShowHeatmapChange(e.target.checked)}
          />
          Mapa cieplna trasy
        </label>
      </div>
    </div>
  );
}
