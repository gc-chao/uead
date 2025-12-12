var engLetters = [ "a", "e", "i", "o", "u", "x", "l", "w", "r" ];
var modLetters = [ "ᵃ", "ᵉ", "ᴵ", "ᴼ", "ᵁ", "ˣ", "ˡ", "ʷ", "ʳ" ];

for (var i = 0; i < engLetters.length; i++)
{
    if (modLetters[i].indexOf(engLetters[i]) > 0 || engLetters[i].indexOf(modLetters[i]) > 0)
    {
        console.warn("Engligh letter '" + engLetters[i] + "' equals with modifier letter '" + modLetters[i] + "'.");
    }
}

for (var i = 0; i < engLetters.length; i++)
{
    var regexp1 = new RegExp("/" + modLetters[i] + "/");
    var regexp2 = new RegExp("/" + engLetters[i] + "/");

    if (regexp1.test(engLetters[i]) || regexp2.test(modLetters[i]))
    {
        console.warn("Engligh string '" + engLetters[i] + "' equals with modifier string '" + modLetters[i] + "'.");
    }
}

console.warn("Test completed.");
