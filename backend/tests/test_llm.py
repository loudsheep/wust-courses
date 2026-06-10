from app.services.llm import extract_text_content

def test_extract_text_content_string():
    assert extract_text_content("Hello") == "Hello"

def test_extract_text_content_list():
    content = [
        {'type': 'text', 'text': "Hello! I'm doing well."},
        {'type': 'text', 'text': " How can I help?"}
    ]
    assert extract_text_content(content) == "Hello! I'm doing well. How can I help?"

def test_extract_text_content_complex_list():
    # The format provided by the user
    content = [{'type': 'text', 'text': "Hello! I'm doing well, thank you for asking. How are you doing today? Is there anything I can help you with?", 'extras': {'signature': '...'}}]
    expected = "Hello! I'm doing well, thank you for asking. How are you doing today? Is there anything I can help you with?"
    assert extract_text_content(content) == expected

def test_extract_text_content_mixed_list():
    content = ["Part 1", {'type': 'text', 'text': " Part 2"}]
    assert extract_text_content(content) == "Part 1 Part 2"

def test_extract_text_content_other():
    assert extract_text_content(123) == "123"
