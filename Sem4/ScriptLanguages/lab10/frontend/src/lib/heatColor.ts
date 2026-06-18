type ColorStop = { t: number; rgb: [number, number, number]; alpha: number }

const WARM_STOPS: ColorStop[] = [
  { t: 0, rgb: [120, 40, 30], alpha: 0.15 },
  { t: 0.35, rgb: [200, 70, 30], alpha: 0.45 },
  { t: 0.65, rgb: [255, 140, 30], alpha: 0.7 },
  { t: 1, rgb: [255, 235, 80], alpha: 0.95 },
]

function lerp(a: number, b: number, f: number): number {
  return a + (b - a) * f
}

export function heatColor(t: number): string {
  const clamped = Math.min(1, Math.max(0, t))

  let lower = WARM_STOPS[0]
  let upper = WARM_STOPS[WARM_STOPS.length - 1]
  for (let i = 0; i < WARM_STOPS.length - 1; i++) {
    if (clamped >= WARM_STOPS[i].t && clamped <= WARM_STOPS[i + 1].t) {
      lower = WARM_STOPS[i]
      upper = WARM_STOPS[i + 1]
      break
    }
  }

  const span = upper.t - lower.t
  const f = span === 0 ? 0 : (clamped - lower.t) / span

  const r = Math.round(lerp(lower.rgb[0], upper.rgb[0], f))
  const g = Math.round(lerp(lower.rgb[1], upper.rgb[1], f))
  const b = Math.round(lerp(lower.rgb[2], upper.rgb[2], f))
  const a = lerp(lower.alpha, upper.alpha, f)

  return `rgba(${r}, ${g}, ${b}, ${a.toFixed(2)})`
}

export function heatWeight(t: number): number {
  return 1.5 + Math.min(1, Math.max(0, t)) * 5.5
}
