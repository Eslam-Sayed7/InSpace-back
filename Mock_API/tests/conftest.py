"""Pytest configuration and fixtures."""
import sys
from unittest.mock import MagicMock

# Mock pyautogui at import time since it requires GUI libraries
sys.modules['pyautogui'] = MagicMock()
