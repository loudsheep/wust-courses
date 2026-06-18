const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8000'

export interface Stop {
  stop_id: string
  stop_code: string | null
  stop_name: string
  stop_lat: number
  stop_lon: number
}

export interface DirectionCount {
  direction: string
  count: number
}

export interface StopDetail extends Stop {
  line_count: number
  departure_count: number
  earliest_departure: string | null
  latest_departure: string | null
  top_directions: DirectionCount[]
}

export interface HeatmapEdge {
  from_stop_id: string
  from_lat: number
  from_lon: number
  to_stop_id: string
  to_lat: number
  to_lon: number
  frequency: number
}

export async function fetchStops(): Promise<Stop[]> {
  const res = await fetch(`${API_URL}/stops`)
  if (!res.ok) throw new Error('Failed to fetch stops')
  return res.json()
}

export async function fetchStopDetail(stopId: string): Promise<StopDetail> {
  const res = await fetch(`${API_URL}/stops/${stopId}`)
  if (!res.ok) throw new Error('Failed to fetch stop detail')
  return res.json()
}

export async function fetchHeatmap(limit = 10000): Promise<HeatmapEdge[]> {
  const res = await fetch(`${API_URL}/network/heatmap?limit=${limit}`)
  if (!res.ok) throw new Error('Failed to fetch heatmap')
  return res.json()
}
