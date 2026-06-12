import pytest
from datetime import datetime

from timeseries import TimeSeries
from measurements import Measurements
from detectors import OutlierDetector, ZeroSpikeDetector, SimpleReporter, SeriesValidator


@pytest.fixture
def mock_series() -> list[TimeSeries]:
    return [
        TimeSeries(
            indicator="temp",
            station_code="ST01",
            averaging_time="daily",
            datetimes=[
                datetime(2024, 1, 1),
                datetime(2024, 1, 2),
                datetime(2024, 1, 3),
            ],
            values=[10.0, 0.0, 0.0],
        )
    ]


@pytest.fixture
def empty_measurements() -> Measurements:
    m = Measurements.__new__(Measurements)

    m.file_metadata = []
    m._cache = {
        0: {
            "full": None,
            "stations": {},
            "parameters": {},
        }
    }

    return m


@pytest.mark.parametrize(
    "analyzer",
    [
        OutlierDetector(k=0.5),
        ZeroSpikeDetector(n=2),
        SimpleReporter(),
    ],
)
def test_detect_all_anomalies_with_mock_data(
    empty_measurements: Measurements, mock_series: list[TimeSeries], analyzer: SeriesValidator
) -> None:
    m = empty_measurements

    # wstrzykujemy dane do cache
    m._cache[0]["full"] = mock_series

    result = m.detect_all_anomalies([analyzer], preload=False)

    assert isinstance(result, dict)

    for _, messages in result.items():
        assert isinstance(messages, list)
        assert all(isinstance(msg, str) for msg in messages)
