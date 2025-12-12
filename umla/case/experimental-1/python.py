import sys
import os
import re

engLetters = [ "a", "e", "i", "o", "u", "x", "l", "w", "r" ];
modLetters = [ "ᵃ", "ᵉ", "ᴵ", "ᴼ", "ᵁ", "ˣ", "ˡ", "ʷ", "ʳ" ];

for i in range(9):
    if modLetters[i].find(engLetters[i]) > -1 or engLetters[i].find(modLetters[i]) > -1:
        print("Engligh letter '" + engLetters[i] + "' equals with modifier letter '" + modLetters[i] + "'.");

for i in range(9):
    regexp1 = re.compile(modLetters[i]);
    regexp2 = re.compile(engLetters[i]);
    
    if re.match(regexp1, engLetters[i]) or re.match(regexp2, modLetters[i]):
        print("Engligh string '" + engLetters[i] + "' equals with modifier string '" + modLetters[i] + "'.");

print("Test completed.");
