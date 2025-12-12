
#include "stdafx.h"
#include "libumlec.h"

bool hasInitialized = false;
std::unordered_map<wchar_t, wchar_t> gFindingTable;

static void InitFindingTable(void)
{
    /*
    All Modifier Letters
    ᴬ 0x1d2c
    ᵃ 0x1d43
    ᴮ 0x1d2e
    ᵇ 0x1d47
    ᶜ 0x1d9c
    ᴰ 0x1d30
    ᵈ 0x1d48
    ᴱ 0x1d31
    ᵉ 0x1d49
    ᶠ 0x1da0
    ᴳ 0x1d33
    ᵍ 0x1d4d
    ʰ 0x02b0
    ᴴ 0x1d34
    ᴵ 0x1d35
    ʲ 0x02b2
    ᴶ 0x1d36
    ᴷ 0x1d37
    ᵏ 0x1d4f
    K 0x212a
    ˡ 0x02e1
    ᴸ 0x1d38
    ᴹ 0x1d39
    ᵐ 0x1d50
    ᴺ 0x1d3a
    ᴼ 0x1d3c
    ᵒ 0x1d52
    ᴾ 0x1d3e
    ᵖ 0x1d56
    ʳ 0x02b3
    ᴿ 0x1d3f
    ˢ 0x02e2
    ᵀ 0x1d40
    ᵗ 0x1d57
    ᵁ 0x1d41
    ᵘ 0x1d58
    ᵛ 0x1d5b
    ⱽ 0x2c7d
    ʷ 0x02b7
    ᵂ 0x1d42
    ˣ 0x02e3
    ʸ 0x02b8
    ᶻ 0x1dbb
    */

    gFindingTable[0x1d2c] = 'a';
    gFindingTable[0x1d43] = 'a';
    gFindingTable[0x1d2e] = 'b';
    gFindingTable[0x1d47] = 'b';
    gFindingTable[0x1d9c] = 'c';
    gFindingTable[0x1d30] = 'd';
    gFindingTable[0x1d48] = 'd';
    gFindingTable[0x1d31] = 'e';
    gFindingTable[0x1d49] = 'e';
    gFindingTable[0x1da0] = 'f';
    gFindingTable[0x1d33] = 'g';
    gFindingTable[0x1d4d] = 'g';
    gFindingTable[0x02b0] = 'h';
    gFindingTable[0x1d34] = 'h';
    gFindingTable[0x1d35] = 'i';
    gFindingTable[0x02b2] = 'j';
    gFindingTable[0x1d36] = 'j';
    gFindingTable[0x1d37] = 'k';
    gFindingTable[0x1d4f] = 'k';
    gFindingTable[0x212a] = 'k';
    gFindingTable[0x02e1] = 'l';
    gFindingTable[0x1d38] = 'l';
    gFindingTable[0x1d39] = 'm';
    gFindingTable[0x1d50] = 'm';
    gFindingTable[0x1d3a] = 'n';
    gFindingTable[0x1d3c] = 'o';
    gFindingTable[0x1d52] = 'o';
    gFindingTable[0x1d3e] = 'p';
    gFindingTable[0x1d56] = 'p';
    gFindingTable[0x02b3] = 'r';
    gFindingTable[0x1d3f] = 'r';
    gFindingTable[0x02e2] = 's';
    gFindingTable[0x1d40] = 't';
    gFindingTable[0x1d57] = 't';
    gFindingTable[0x1d41] = 'u';
    gFindingTable[0x1d58] = 'u';
    gFindingTable[0x1d5b] = 'v';
    gFindingTable[0x2c7d] = 'v';
    gFindingTable[0x02b7] = 'w';
    gFindingTable[0x1d42] = 'w';
    gFindingTable[0x02e3] = 'x';
    gFindingTable[0x02b8] = 'y';
    gFindingTable[0x1dbb] = 'z';
}

LIBUMLEC_API bool IsModifierLetterExistInString(LPWSTR lpStr, int nLength)
{
    if (!hasInitialized)
    {
        InitFindingTable();

        hasInitialized = true;
    }

    if (nLength < 0)
        nLength = wcslen(lpStr);

    for (int i = 0; i < nLength; i++)
    {
        wchar_t ch = lpStr[i];

        if (gFindingTable.find(ch) != gFindingTable.end())
            return true;
    }

    return false;
}

LIBUMLEC_API int ConvertModifierLettersInString(LPWSTR lpStr, int nLength)
{
    if (!hasInitialized)
    {
        InitFindingTable();

        hasInitialized = true;
    }

    if (nLength < 0)
        nLength = wcslen(lpStr);

    for (int i = 0; i < nLength; i++)
    {
        wchar_t ch = lpStr[i];

        if (gFindingTable.find(ch) != gFindingTable.end())
            lpStr[i] = gFindingTable[ch];
    }

    return 0;
}