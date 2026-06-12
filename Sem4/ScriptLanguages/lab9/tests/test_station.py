import pytest
from station import Station


def test_station_eq_same_code() -> None:
    s1 = Station(id="A1")
    s2 = Station(id="A1")

    assert s1 == s2


def test_station_eq_different_code() -> None:
    s1 = Station(id="A1")
    s2 = Station(id="B2")

    assert s1 != s2


def test_station_eq_with_non_station_object() -> None:
    s1 = Station(id="A1")
    other = "A1"

    assert s1 != other


@pytest.mark.parametrize(
    "id1, id2, expected",
    [
        ("X1", "X1", True),
        ("X1", "X2", False),
        ("ABC", "ABC", True),
        ("ABC", "DEF", False),
    ],
)
def test_station_eq_parametrized(id1: str, id2: str, expected: bool) -> None:
    s1 = Station(id=id1)
    s2 = Station(id=id2)

    assert (s1 == s2) is expected
