import pytest
import math
from datetime import datetime
from timeseries import TimeSeries


@pytest.fixture
def ts() -> TimeSeries:
    return TimeSeries(
        indicator="temp",
        station_code="ST01",
        averaging_time="hourly",
        datetimes=[
            datetime(2024, 1, 1, 12),
            datetime(2024, 1, 1, 13),
            datetime(2024, 1, 2, 14),
        ],
        values=[10.0, 20.0, 30.0],
    )


def test_getitem_integer_index(ts: TimeSeries) -> None:
    result = ts[1]

    assert result == (ts.datetimes[1], ts.values[1])


def test_getitem_slice(ts: TimeSeries) -> None:
    result = ts[0:2]

    expected = list(
        zip(
            ts.datetimes[0:2],
            ts.values[0:2],
        )
    )

    assert result == expected


def test_getitem_datetime_existing(ts: TimeSeries) -> None:
    dt = datetime(2024, 1, 1, 13)

    result = ts[dt]

    assert result == 20.0


def test_getitem_datetime_missing(ts: TimeSeries) -> None:
    dt = datetime(2025, 1, 1, 0)

    with pytest.raises(KeyError):
        _ = ts[dt]


def test_stats_full_data(ts: TimeSeries) -> None:

    assert ts.mean == 20.0

    expected_std = math.sqrt(((10 - 20) ** 2 + (20 - 20) ** 2 + (30 - 20) ** 2) / 3)
    assert ts.stddev == expected_std


def test_stats_with_none_values() -> None:
    ts = TimeSeries(
        indicator="temp",
        station_code="ST02",
        averaging_time="daily",
        datetimes=[
            datetime(2024, 1, 1),
            datetime(2024, 1, 2),
            datetime(2024, 1, 3),
            datetime(2024, 1, 4),
        ],
        values=[10.0, None, 30.0, None],
    )

    assert ts.mean == 20.0

    expected_std = math.sqrt(((10 - 20) ** 2 + (30 - 20) ** 2) / 2)
    assert ts.stddev == expected_std


def test_getitem_invalid_slice(ts: TimeSeries) -> None:
    with pytest.raises(TypeError):
        _ = ts["invalid"]


def test_out_of_range_slice(ts: TimeSeries) -> None:
    with pytest.raises(ValueError):
        _ = ts[10:20:0]
