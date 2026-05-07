from collections import namedtuple
from typing import List, Tuple
import logging

Measurement = namedtuple('Measurement', ['timestamp', 'value'])

logger = logging.getLogger(__name__)

def detect_anomalies(measurements: List[Measurement], alarm_threshold, delta_threshold, zero_threshold) -> List[str]:
    anomalies = []

    for i in range(1, len(measurements)):
        delta = abs(measurements[i].value - measurements[i - 1].value)
        if delta > delta_threshold:
            anomalies.append(f"Nagły skok wartości między {measurements[i-1].timestamp} a {measurements[i].timestamp}: {measurements[i-1].value} -> {measurements[i].value}")

    zero_count = sum(1 for m in measurements if m.value <= 0)
    if zero_count > zero_threshold:
        anomalies.append(f"Za dużo zerowych lub ujemnych wartości ({zero_count})")

    for m in measurements:
        if m.value > alarm_threshold:
            anomalies.append(f"Skok powyżej progu alarmowego w {m.timestamp}: {m.value} > {alarm_threshold}")

    return anomalies
