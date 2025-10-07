from pydantic import BaseModel

class ClickRequest(BaseModel):
    x: int
    y: int

class ScrollRequest(BaseModel):
    amount: int

class WriteRequest(BaseModel):
    text: str
    
class KeyPressRequest(BaseModel):
    key: str

class HotkeyRequest(BaseModel):
    keys: list[str]