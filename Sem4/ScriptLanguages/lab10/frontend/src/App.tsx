import { MapView } from "@/components/MapView";
import { FileUpload } from "@/components/FileUpload";

function App() {
  return (
    <div className="relative h-full w-full">
      <MapView />

      <div className="pointer-events-none absolute top-0 right-0 z-[1000] p-4">
        <div className="pointer-events-auto rounded-lg border border-white/10 bg-black/60 px-4 py-3 backdrop-blur-sm">
          <h1 className="text-lg font-semibold tracking-tight text-white">
            Rozkład jazdy — Wrocław
          </h1>
          <p className="text-sm text-gray-400">GTFS Timetable Explorer</p>

          <FileUpload />
        </div>
      </div>
    </div>
  );
}

export default App;
