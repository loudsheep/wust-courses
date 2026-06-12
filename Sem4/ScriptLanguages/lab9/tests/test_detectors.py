from datetime import datetime
from timeseries import TimeSeries
from detectors import OutlierDetector, ZeroSpikeDetector, ThresholdDetector


def test_outlier_detector_detects_outlier() -> None:

    series = TimeSeries(
        indicator="temp",
        station_code="ST01",
        averaging_time="daily",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
            datetime(2024, 1, 3),
            datetime(2024, 1, 4),
            datetime(2024, 1, 5),
            datetime(2024, 1, 6),
            datetime(2024, 1, 7),
            datetime(2024, 1, 8),
            datetime(2024, 1, 9),
            datetime(2024, 1, 10),
            datetime(2024, 1, 11),
            datetime(2024, 1, 12),
            datetime(2024, 1, 13),
        ],
        values=[
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            100.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
            10.0,
        ],
    )

    detector = OutlierDetector(k=3)

    result = detector.analyze(series)

    assert len(result) == 1

    assert "Outlier detected" in result[0]
    assert "100.0" in result[0]
    assert "2024-01-07" in result[0]


def test_zero_spike_detector_detects_three_consecutive_zeros_or_none() -> None:
    series = TimeSeries(
        indicator="temp",
        station_code="ST01",
        averaging_time="daily",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
            datetime(2024, 1, 3),
            datetime(2024, 1, 4),
        ],
        values=[
            1.0,
            0.0,
            None,
            2.0,
        ],
    )

    detector = ZeroSpikeDetector(n=3)

    result = detector.analyze(series)

    assert result == []


def test_zero_spike_detector_detects_spike() -> None:
    series = TimeSeries(
        indicator="temp",
        station_code="ST02",
        averaging_time="daily",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
            datetime(2024, 1, 3),
            datetime(2024, 1, 4),
        ],
        values=[0.0, None, 0.0, 5.0],
    )

    detector = ZeroSpikeDetector(n=3)

    result = detector.analyze(series)

    assert len(result) == 1
    assert "Zero spike detected" in result[0]
    assert "3 consecutive zeros/missing values" in result[0]


def test_zero_spike_detector_detects_spike_at_end() -> None:
    series = TimeSeries(
        indicator="temp",
        station_code="ST03",
        averaging_time="daily",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
            datetime(2024, 1, 3),
        ],
        values=[1.0, 0.0, None],
    )

    detector = ZeroSpikeDetector(n=2)

    result = detector.analyze(series)

    assert any("end of series" in r for r in result)


def test_threshold_detector_detects_exceeding_values() -> None:
    series = TimeSeries(
        indicator="pressure",
        station_code="ST01",
        averaging_time="hourly",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
            datetime(2024, 1, 3),
        ],
        values=[10.0, 50.0, 120.0],
    )

    detector = ThresholdDetector(threshold=100.0)

    result = detector.analyze(series)

    assert len(result) == 1
    assert "Threshold exceeded" in result[0]
    assert "120.0" in result[0]
    assert "100.0" in result[0]


def test_threshold_detector_no_exceeding_values() -> None:
    series = TimeSeries(
        indicator="pressure",
        station_code="ST01",
        averaging_time="hourly",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
        ],
        values=[10.0, 20.0],
    )

    detector = ThresholdDetector(threshold=100.0)

    result = detector.analyze(series)

    assert result == []
