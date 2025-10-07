"""Unit tests for controller functions with mocked pyautogui."""
import pytest
from unittest.mock import Mock, patch, call
from app.controller import (
    perform_left_click,
    perform_right_click,
    perform_double_click,
    perform_move,
    perform_scroll_up,
    perform_scroll_down,
    perform_write,
    perform_keypress,
    perform_hotkey
)


class TestClickOperations:
    """Test click-related operations."""

    @patch('app.controller.pyautogui')
    def test_perform_left_click(self, mock_pyautogui):
        """Test left click performs correct action."""
        # Arrange
        x, y = 100, 200

        # Act
        result = perform_left_click(x, y)

        # Assert
        mock_pyautogui.click.assert_called_once_with(x=100, y=200, button="left")
        assert result == {"status": "left clicked", "x": 100, "y": 200}

    @patch('app.controller.pyautogui')
    def test_perform_right_click(self, mock_pyautogui):
        """Test right click performs correct action."""
        # Arrange
        x, y = 150, 250

        # Act
        result = perform_right_click(x, y)

        # Assert
        mock_pyautogui.click.assert_called_once_with(x=150, y=250, button="right")
        assert result == {"status": "right clicked", "x": 150, "y": 250}

    @patch('app.controller.pyautogui')
    def test_perform_double_click(self, mock_pyautogui):
        """Test double click performs correct action."""
        # Arrange
        x, y = 300, 400

        # Act
        result = perform_double_click(x, y)

        # Assert
        mock_pyautogui.doubleClick.assert_called_once_with(x=300, y=400, button="left")
        assert result == {"status": "double clicked", "x": 300, "y": 400}


class TestMouseMovement:
    """Test mouse movement operations."""

    @patch('app.controller.pyautogui')
    def test_perform_move(self, mock_pyautogui):
        """Test mouse move performs correct action."""
        # Arrange
        x, y = 500, 600

        # Act
        result = perform_move(x, y)

        # Assert
        mock_pyautogui.moveTo.assert_called_once_with(500, 600)
        assert result == {"status": "moved", "x": 500, "y": 600}


class TestScrollOperations:
    """Test scroll-related operations."""

    @patch('app.controller.pyautogui')
    def test_perform_scroll_up(self, mock_pyautogui):
        """Test scroll up performs correct action."""
        # Arrange
        amount = 10

        # Act
        result = perform_scroll_up(amount)

        # Assert
        mock_pyautogui.scroll.assert_called_once_with(10)
        assert result == {"status": "scrolled up", "amount": 10}

    @patch('app.controller.pyautogui')
    def test_perform_scroll_down(self, mock_pyautogui):
        """Test scroll down performs correct action with negative amount."""
        # Arrange
        amount = 5

        # Act
        result = perform_scroll_down(amount)

        # Assert
        mock_pyautogui.scroll.assert_called_once_with(-5)
        assert result == {"status": "scrolled down", "amount": 5}


class TestKeyboardOperations:
    """Test keyboard-related operations."""

    @patch('app.controller.pyautogui')
    def test_perform_write(self, mock_pyautogui):
        """Test write text performs correct action."""
        # Arrange
        text = "Hello World"

        # Act
        result = perform_write(text)

        # Assert
        mock_pyautogui.write.assert_called_once_with("Hello World")
        assert result == {"status": "written", "text": "Hello World"}

    @patch('app.controller.pyautogui')
    def test_perform_keypress(self, mock_pyautogui):
        """Test key press performs correct action."""
        # Arrange
        key = "enter"

        # Act
        result = perform_keypress(key)

        # Assert
        mock_pyautogui.press.assert_called_once_with("enter")
        assert result == {"status": "key pressed", "key": "enter"}

    @patch('app.controller.pyautogui')
    def test_perform_hotkey(self, mock_pyautogui):
        """Test hotkey performs correct action."""
        # Arrange
        keys = ["ctrl", "c"]

        # Act
        result = perform_hotkey(keys)

        # Assert
        mock_pyautogui.hotkey.assert_called_once_with("ctrl", "c")
        assert result == {"status": "hotkey pressed", "keys": ["ctrl", "c"]}

    @patch('app.controller.pyautogui')
    def test_perform_hotkey_three_keys(self, mock_pyautogui):
        """Test hotkey with three keys performs correct action."""
        # Arrange
        keys = ["ctrl", "shift", "esc"]

        # Act
        result = perform_hotkey(keys)

        # Assert
        mock_pyautogui.hotkey.assert_called_once_with("ctrl", "shift", "esc")
        assert result == {"status": "hotkey pressed", "keys": ["ctrl", "shift", "esc"]}


class TestEdgeCases:
    """Test edge cases and boundary conditions."""

    @patch('app.controller.pyautogui')
    def test_perform_left_click_zero_coordinates(self, mock_pyautogui):
        """Test left click at origin (0, 0)."""
        # Act
        result = perform_left_click(0, 0)

        # Assert
        mock_pyautogui.click.assert_called_once_with(x=0, y=0, button="left")
        assert result == {"status": "left clicked", "x": 0, "y": 0}

    @patch('app.controller.pyautogui')
    def test_perform_scroll_up_zero_amount(self, mock_pyautogui):
        """Test scroll up with zero amount."""
        # Act
        result = perform_scroll_up(0)

        # Assert
        mock_pyautogui.scroll.assert_called_once_with(0)
        assert result == {"status": "scrolled up", "amount": 0}

    @patch('app.controller.pyautogui')
    def test_perform_write_empty_string(self, mock_pyautogui):
        """Test write with empty string."""
        # Act
        result = perform_write("")

        # Assert
        mock_pyautogui.write.assert_called_once_with("")
        assert result == {"status": "written", "text": ""}

    @patch('app.controller.pyautogui')
    def test_perform_hotkey_single_key(self, mock_pyautogui):
        """Test hotkey with single key."""
        # Act
        result = perform_hotkey(["a"])

        # Assert
        mock_pyautogui.hotkey.assert_called_once_with("a")
        assert result == {"status": "hotkey pressed", "keys": ["a"]}
