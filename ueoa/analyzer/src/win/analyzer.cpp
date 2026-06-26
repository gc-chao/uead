
#include <stdio.h>
#include <windows.h>
#include <Windns.h>

#include "cmpfun.h"

#define MAX_FILES 40960
#define MAX_IGNORABLE_CHARS 4600

struct FileName
{
    char name[255];
};

FileName fileList[1024 * 1024] = { 0 };

static int GetModifierLetters(WCHAR uml[])
{
    int count = 0;
    WCHAR latin[2] = { L'?', 0 };
    WCHAR unicode[2] = { L'?', 0 };

    for (wchar_t ch = L'a'; ch <= 'z'; ch++)
    {
        latin[0] = ch;

        for (int i = 128; i < 65536; i++)
        {
            unicode[0] = i;

            if (CSTR_EQUAL == CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, unicode, -1, latin, -1))
                uml[count++] = i;
        }
    }

    return count;
}

static int GetLatinAndModifierLetters(WCHAR letters[])
{
    int count = 0;
    count += GetModifierLetters(letters);

    for (WCHAR ch = L'a'; ch <= 'z'; ch++)
        letters[count++] = ch;

    for (WCHAR ch = L'A'; ch <= 'Z'; ch++)
        letters[count++] = ch;

    return count;
}

static int BinaryCompare(const char * pBuffer1, const char * pBuffer2, int length)
{
    for (int i = 0; i < length; i++)
    {
        if (pBuffer1[i] != pBuffer2[i])
            return FALSE;
    }

    return TRUE;
}

static int FindStringInFile(FILE * pFile, const char * pString, int length)
{
    int i = -1;
    char buffer[255] = { 0 };

    while (true)
    {
        i++;

        if (0 != (fseek(pFile, i, SEEK_SET)))
            return FALSE;

        int result;

        if ((result = fread(buffer, 1, length, pFile)) < length)
            return FALSE;

        if (BinaryCompare(buffer, pString, length))
            return TRUE;
    }
}

static void ListFiles(LPCSTR pDirectory, FileName * pFileList)
{
    int index = 0;
    WIN32_FIND_DATA fd;
    HANDLE hFind = INVALID_HANDLE_VALUE;

    hFind = FindFirstFileA(pDirectory, &fd);

    if (hFind == INVALID_HANDLE_VALUE)
        return;

    do
    {
        if (strcmp(fd.cFileName, ".") != 0 && strcmp(fd.cFileName, "..") != 0)
        if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
            strcpy(pFileList[index++].name, fd.cFileName);
    } while (FindNextFileA(hFind, &fd) != 0);

    FindClose(hFind);
}

void CompareStringSimpleTest()
{
    if (CSTR_EQUAL != CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, L"ˢ", -1, L"s", -1))
        printf("Result of CompareStringW with different 's' : not equal\n");
    else
        printf("Result of CompareStringW with different 's' : equal\n");

    if (CSTR_EQUAL != CompareStringEx(NULL, NORM_IGNORECASE, L"ˢ", -1, L"s", -1, NULL, NULL, NULL))
        printf("Result of CompareStringEx with different 's' : not equal\n");
    else
        printf("Result of CompareStringEx with different 's' : equal\n");
}

void CompareStringComplexTest(PCWCHAR str1, PCWCHAR str2)
{
    if (0 == wcscoll(str1, str2))
        wprintf(L"wcscoll(\"%s\", \"%s\") -> equal\n", str1, str2);
    else
        wprintf(L"wcscoll(\"%s\", \"%s\") -> not equal\n", str1, str2);

    if (0 == wcscmp(str1, str2))
        wprintf(L"wcscmp(\"%s\", \"%s\") -> equal\n", str1, str2);
    else
        wprintf(L"wcscmp(\"%s\", \"%s\") -> not equal\n", str1, str2);

    if (0 == wcsnicmp(str1, str2, 255))
        wprintf(L"wcsnicmp(\"%s\", \"%s\") -> equal\n", str1, str2);
    else
        wprintf(L"wcsnicmp(\"%s\", \"%s\") -> not equal\n", str1, str2);

    if (0 == wcsicmp(str1, str2))
        wprintf(L"wcsicmp(\"%s\", \"%s\") -> equal\n", str1, str2);
    else
        wprintf(L"wcsicmp(\"%s\", \"%s\") -> not equal\n", str1, str2);

    if (CSTR_EQUAL != CompareStringOrdinal(str1, -1, str2, -1, FALSE))
        wprintf(L"CompareStringOrdinal(\"%s\", \"%s\", FALSE) -> not equal\n", str1, str2);
    else
        wprintf(L"CompareStringOrdinal(\"%s\", \"%s\", FALSE) -> equal\n", str1, str2);

    if (CSTR_EQUAL != CompareStringOrdinal(str1, -1, str2, -1, TRUE))
        wprintf(L"CompareStringOrdinal(\"%s\", \"%s\", TRUE) -> not equal\n", str1, str2);
    else
        wprintf(L"CompareStringOrdinal(\"%s\", \"%s\", TRUE) -> equal\n", str1, str2);

    wprintf(L"CompareStringA(LOCALE_INVARIANT, NORM_IGNORECASE, \"%s\", \"%s\")", str1, str2);

    int flag = -1;

    for (int cp = 0; cp < 65536; cp++)
    {
        char buf1[16] = { 0 };
        char buf2[16] = { 0 };

        WideCharToMultiByte(cp, 0, str1, -1, buf1, 16, NULL, NULL);
        WideCharToMultiByte(CP_ACP, 0, str2, -1, buf2, 16, NULL, NULL);

        if (CSTR_EQUAL == CompareStringA(LOCALE_INVARIANT, NORM_IGNORECASE, buf1, -1, buf2, -1))
        {
            if (flag < 0)
            {
                wprintf(L", [%d]", cp);
                flag = cp;
            }
        }
        else
        {
            if (flag >= 0)
            {
                if (cp != flag + 1)
                    wprintf(L"-[%d]", cp - 1);
                flag = -1;
            }
        }
    }

    wprintf(L"Besides above is not equal!\n");

    if (CSTR_EQUAL != CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, str1, -1, str2, -1))
        wprintf(L"CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, \"%s\", \"%s\") -> not equal\n", str1, str2);
    else
        wprintf(L"CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, \"%s\", \"%s\") -> equal\n", str1, str2);

    if (CSTR_EQUAL != CompareStringEx(NULL, NORM_IGNORECASE, str1, -1, str2, -1, NULL, NULL, NULL))
        wprintf(L"CompareStringEx(LOCALE_INVARIANT, NORM_IGNORECASE, \"%s\", \"%s\") -> not equal\n", str1, str2);
    else
        wprintf(L"CompareStringEx(LOCALE_INVARIANT, NORM_IGNORECASE, \"%s\", \"%s\") -> equal\n", str1, str2);

    wprintf(L"\n");
}

void CompareStringAllComplexTest()
{
    wchar_t str1[3] = { 192, 757, 0 };
    wchar_t str2[6] = { L's', 192, 757, L'v', L'e', 0 };
    wchar_t str3[3] = { 173, 0 };
    wchar_t str4[6] = { L's', 173, L'a', L'v', L'e', 0 };

    CompareStringComplexTest(L"ˢ", L"s");
    CompareStringComplexTest(L"ˢave", L"save");
    CompareStringComplexTest(str1, L"a");
    CompareStringComplexTest(str2, L"save");
    CompareStringComplexTest(str3, L"");
    CompareStringComplexTest(str4, L"save");
}

void IdnToAsciiSimpleTest(void)
{
    wchar_t spoofingIDN1[] = L"ʷʷʷ.ᴹᴵᶜʳᵒˢᵒᶠᵀ.ᶜᵒᴹ";
    wchar_t buffer[255] = { 0 };

    IdnToAscii(IDN_ALLOW_UNASSIGNED, spoofingIDN1, wcslen(spoofingIDN1), buffer, 255);

    wprintf(L"Result of IdnToAscii() with domain obfuscated by modifier letters: %s \n", buffer);
}

void CompareStringWParameterTest(void)
{
    printf("Starting CompareStringW parameter space scanning");

    char info[255];

    int flags[10] = { NORM_IGNORECASE, LINGUISTIC_IGNORECASE, LINGUISTIC_IGNOREDIACRITIC, NORM_IGNOREKANATYPE, NORM_IGNORENONSPACE, NORM_IGNORESYMBOLS, NORM_IGNOREWIDTH, NORM_LINGUISTIC_CASING, SORT_DIGITSASNUMBERS, SORT_STRINGSORT };
    int langs[] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, 0x21, 0x22, 0x23, 
                    0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x32, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3e, 0x3f, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b,
                    0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x5b, 0x5c, 0x5d, 0x5e, 0x5f, 0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x67, 0x68, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x73, 0x75,
                    0x78, 0x7a, 0x7c, 0x7e, 0x7f, 0x80, 0x81, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x8c, 0x91, 0x92 };
    char * langNames[] = { "LANG_NEUTRAL", "LANG_ARABIC", "LANG_BULGARIAN", "LANG_CATALAN LANG_VALENCIAN", "LANG_CHINESE", "LANG_CZECH", "LANG_DANISH", "LANG_GERMAN", "LANG_GREEK", "LANG_ENGLISH", "LANG_SPANISH", "LANG_FINNISH", "LANG_FRENCH",
                           "LANG_HEBREW", "LANG_HUNGARIAN", "LANG_ICELANDIC", "LANG_ITALIAN", "LANG_JAPANESE", "LANG_KOREAN", "LANG_DUTCH", "LANG_NORWEGIAN", "LANG_POLISH", "LANG_PORTUGUESE", "LANG_ROMANSH", "LANG_ROMANIAN", "LANG_RUSSIAN", 
                           "LANG_BOSNIAN LANG_CROATIAN LANG_SERBIAN", "LANG_SLOVAK", "LANG_ALBANIAN", "LANG_SWEDISH", "LANG_THAI", "LANG_TURKISH", "LANG_URDU", "LANG_INDONESIAN", "LANG_UKRAINIAN", "LANG_BELARUSIAN", "LANG_SLOVENIAN", 
                           "LANG_ESTONIAN", "LANG_LATVIAN", "LANG_LITHUANIAN", "LANG_TAJIK", "LANG_FARSI LANG_PERSIAN", "LANG_VIETNAMESE", "LANG_ARMENIAN", "LANG_AZERI LANG_AZERBAIJANI", "LANG_BASQUE", "LANG_LOWER_SORBIAN LANG_UPPER_SORBIAN",
                           "LANG_MACEDONIAN", "LANG_TSWANA", "LANG_XHOSA", "LANG_ZULU", "LANG_AFRIKAANS", "LANG_GEORGIAN", "LANG_FAEROESE", "LANG_HINDI", "LANG_MALTESE", "LANG_SAMI", "LANG_IRISH", "LANG_MALAY", "LANG_KAZAK", "LANG_KYRGYZ", 
                           "LANG_SWAHILI", "LANG_TURKMEN", "LANG_UZBEK", "LANG_TATAR", "LANG_BANGLA LANG_BENGALI", "LANG_PUNJABI", "LANG_GUJARATI", "LANG_ODIA LANG_ORIYA", "LANG_TAMIL", "LANG_TELUGU", "LANG_KANNADA", "LANG_MALAYALAM",
                           "LANG_ASSAMESE", "LANG_MARATHI", "LANG_SANSKRIT", "LANG_MONGOLIAN", "LANG_TIBETAN", "LANG_WELSH", "LANG_KHMER", "LANG_LAO", "LANG_GALICIAN", "LANG_KONKANI", "LANG_MANIPURI", "LANG_SINDHI", "LANG_SYRIAC", "LANG_SINHALESE",
                           "LANG_CHEROKEE", "LANG_INUKTITUT", "LANG_AMHARIC", "LANG_TAMAZIGHT", "LANG_KASHMIRI", "LANG_NEPALI", "LANG_FRISIAN", "LANG_PASHTO", "LANG_FILIPINO", "LANG_DIVEHI", "LANG_FULAH LANG_PULAR", "LANG_HAUSA", "LANG_YORUBA", 
                           "LANG_QUECHUA", "LANG_SOTHO", "LANG_BASHKIR", "LANG_LUXEMBOURGISH", "LANG_GREENLANDIC", "LANG_IGBO", "LANG_TIGRIGNA LANG_TIGRINYA", "LANG_HAWAIIAN", "LANG_YI", "LANG_MAPUDUNGUN", "LANG_MOHAWK", "LANG_BRETON", 
                           "LANG_INVARIANT", "LANG_UIGHUR", "LANG_MAORI", "LANG_OCCITAN", "LANG_CORSICAN", "LANG_ALSATIAN", "LANG_SAKHA LANG_YAKUT", "LANG_KICHE", "LANG_KINYARWANDA", "LANG_WOLOF", "LANG_DARI", "LANG_SCOTTISH_GAELIC", "LANG_CENTRAL_KURDISH" };

    FILE * pFile = fopen("TestReport1.csv", "w");

    sprintf(info, "flag,locale,sordId,langId,subLangId\n");
    fwrite(info, strlen(info), 1, pFile);

    for (int flagId = 0; flagId < 10; flagId++)
    {
        for (int langId = 0; langId < 125; langId++)
        {
            for (int subLangId = 0; subLangId < 0x16; subLangId++)
            {
                for (int sortId = 0; sortId < 5; sortId++)
                {
                    int locale = MAKELCID(MAKELANGID(langs[langId], subLangId), sortId);

                    bool equals = CSTR_EQUAL == CompareStringW(locale, flags[flagId], L"ˢ", -1, L"S", -1);

                    if (equals)
                    {
                        sprintf(info, "0x%x[%s],0x%x,0x%x[%s],0x%x[%s](%d),0x%x\n",
                            flags[flagId],
                            (flags[flagId] == 0x01 ? "NORM_IGNORECASE" : (flags[flagId] == 0x10 ? "LINGUISTIC_IGNORECASE" : "OTHER")),
                            locale,
                            sortId,
                            sortId == 0 ? "SORT_DEFAULT" : "OTHER",
                            langs[langId],
                            langNames[langId],
                            langId,
                            subLangId);

                        fwrite(info, strlen(info), 1, pFile);
                    }
                }
            }
        }

        printf(".");
    }

    fclose(pFile);

    printf("\nFinished!\n");
}

void SingleWideCharComparsionTest(void)
{
    WCHAR info[255] = { 0 };

    FILE * pFile = fopen("TestReport2.txt", "w");

    for (int i = 0; i < 65536; i++)
    {
        WCHAR wch1[2] = { 0 };

        *((USHORT*)wch1) = (USHORT)i;

        bool display = false;

        for (int j = 0; j < 65536; j++)
        {
            WCHAR wch2[2] = { 0 };

            *((USHORT*)wch2) = (USHORT)j;

            if (i == j)
                continue;

            if (CSTR_EQUAL == CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, wch1, -1, wch2, -1))
            {
                if (!display)
                {
                    display = true;

                    fprintf(pFile, "u+%x: ", i, wch1[0]);
                }

                fprintf(pFile, "u+%x ", j, wch2[0]);
            }
        }

        if (display)
            fprintf(pFile, "\n");

        if (i % 1000 == 0)
            printf("%.0f%% ", i * 100.0f / 65536);
    }

    fclose(pFile);

    printf("\nFinished!\n");
}

void SystemProgramScanning(const char * lpszAPIName)
{
    char path[255] = { 0 };
    char extn[255] = { 0 };
    char file[255] = { 0 };

    int iFlagLen = strlen(lpszAPIName);

    char name[255] = { 0 };
    sprintf(name, "TestReport3_%s.csv", lpszAPIName);

    FILE * pFile = fopen(name, "w");

    printf("Please enter scanning path (e.g. 'C:\\Windows\\System32\\' or 'C:\\Windows\\SysWOW64\\'):");
    scanf("%s", path);

    printf("Please enter file extension (e.g. '*.exe'):");
    scanf("%s", extn);

    sprintf(file, "%s%s", path, extn);

    printf("Scanning path: %s...\n", file);

    ListFiles(file, fileList);

    for (int i = 0; i < MAX_FILES; i++)
    {
        char buffer[255] = { 0 };

        sprintf(buffer, "%s%s", path, fileList[i].name);

        FILE * pTestFile = fopen(buffer, "rb");

        if (pTestFile == NULL)
            continue;

        printf(".");

        if (FindStringInFile(pTestFile, lpszAPIName, iFlagLen))
        {
            printf("\nAt-risk Program: %s\n", fileList[i].name);
            fprintf(pFile, "%s, %s\n", fileList[i].name, buffer);
        }

        fclose(pTestFile);
    }

    fclose(pFile);

    printf("\nFinished!\n");
}

int ScanIgnorableChars(pfnIsEqual fun, WCHAR chars[], int nSize)
{
    int count = 0;
    FILE * pFile = NULL;

    WCHAR latin[2] = { L'?', 0 };
    WCHAR unicode[4] = { L'?', 0 };

    if (chars == NULL)
        pFile = fopen("report_ignorable_chars.txt", "w");

    for (int i = 1; i < 65536; i++)
    {
        bool equal = true;

        for (WCHAR ch = L'a'; ch <= L'z'; ch++)
        {
            latin[0] = ch;
            unicode[0] = i;
            unicode[1] = ch;
            unicode[2] = i;

            bool equals = fun(unicode, latin);

            if (!equals)
            {
                equal = false;
                break;
            }
        }

        if (equal == true)
        {
            if ((chars != NULL) && (count > nSize))
                printf("chars array too small for [%d].", count);

            if (chars != NULL)
                chars[count++] = i;
            else
            {
                char ansi[8] = { 0 };
                WideCharToMultiByte(CP_UTF8, 0, unicode, 1, ansi, 255, NULL, NULL);

                count++;

                fprintf(pFile, "%d;", i);
            }
        }
    }

    if (chars == NULL)
    {
        printf(" %d found!\n", count);

        fclose(pFile);
    }

    if (chars != NULL)
        printf("\nFinished!\n");

    return count;
}

void ScanCharEquals1(pfnIsEqual fun)
{
    FILE * pFile1 = fopen("report_char_eqauls_1c.txt", "w");
    FILE * pFile2 = fopen("report_char_eqauls_1i.txt", "w");

    WCHAR latin[2] = { L'?', 0 };
    WCHAR unicode[10] = { L'?', 0 };

    for (wchar_t ch = L'a'; ch <= 'z'; ch++)
    {
        latin[0] = ch;

        wprintf(L"\n0x%x %s ", ch, latin);
        fwrite(latin, 1, 1, pFile1);
        fwrite(latin, 1, 1, pFile2);

        for (int i = 1; i < 65536; i++)
        {
            *((USHORT*)unicode) = (USHORT)i;

            bool equals = fun(unicode, latin);

            if (equals)
            {
                char ansi[255] = { 0 };
                WideCharToMultiByte(CP_UTF8, 0, unicode, -1, ansi, 255, NULL, NULL);

                wprintf(L" (0x%x %s)", i, unicode);

                fprintf(pFile1, ";%s", ansi);
                fprintf(pFile2, ";%d", i);
            }
        }

        fwrite("\n", 1, 1, pFile1);
        fwrite("\n", 1, 1, pFile2);

        fflush(pFile1);
        fflush(pFile2);
    }

    fclose(pFile2);
    fclose(pFile1);

    printf("\nFinished!\n");
}

void ScanCharEquals2(pfnIsEqual fun)
{
    FILE * pFile1 = fopen("report_char_eqauls_2c.txt", "w");
    FILE * pFile2 = fopen("report_char_eqauls_2i.txt", "w");

    WCHAR latin[2] = { L'?', 0 };
    WCHAR unicode[3] = { L'?', L'?', 0 };

    WCHAR ignorableChars[MAX_IGNORABLE_CHARS] = { 0 };
    ScanIgnorableChars(&IsCompareStringWEquals, ignorableChars, MAX_IGNORABLE_CHARS);

    printf(".\n");

    for (wchar_t ch = L'a'; ch <= 'z'; ch++)
    {
        int ptr1 = 0;

        latin[0] = ch;

        wprintf(L"\n0x%x %s ", ch, latin);
        fwrite(latin, 1, 1, pFile1);
        fwrite(latin, 1, 1, pFile2);

        int count = 0;

        for (int i = 1; i < 65536; i++)
        {
            if (i == ignorableChars[ptr1])
            {
                ptr1++;
                continue;
            }

            int ptr2 = 0;

            for (int j = 1; j < 65536; j++)
            {
                if (j == ignorableChars[ptr2])
                {
                    ptr2++;
                    continue;
                }

                unicode[0] = i;
                unicode[1] = j;

                bool equals = fun(unicode, latin);

                if (equals)
                {
                    count++;

                    char ansi[8] = { 0 };
                    int result = WideCharToMultiByte(CP_UTF8, 0, unicode, -1, ansi, 255, NULL, NULL);

                    if (result == 0)
                        printf("ERROR: (%d %d)", ch, i);

                    fprintf(pFile1, ";%s", ansi);
                    fprintf(pFile2, ";%d,%d", i, j);
                }
            }

            if (i % 5000 == 0)
            {
                printf(".");
                fflush(pFile1);
                fflush(pFile2);
            }
        }

        wprintf(L"%d ", count);

        fwrite("\n", 1, 1, pFile1);
        fwrite("\n", 1, 1, pFile2);

        Sleep(0);
    }

    fclose(pFile1);
    fclose(pFile2);

    printf("\nFinished!\n");
}

static void FullStringComparsionValidation()
{
    printf("IsWcsCmpEquals");
    ScanIgnorableChars(&IsWcsCmpEquals, NULL, 0);
    printf("IsWcsNCmpEquals");
    ScanIgnorableChars(&IsWcsNCmpEquals, NULL, 0);
    printf("IsWcsCollEquals");
    ScanIgnorableChars(&IsWcsCollEquals, NULL, 0);
    printf("IsWcsIcmpEquals");
    ScanIgnorableChars(&IsWcsIcmpEquals, NULL, 0);
    printf("IsWcsNICmpEquals");
    ScanIgnorableChars(&IsWcsNICmpEquals, NULL, 0);
    printf("IsWcsICmpLEquals");
    ScanIgnorableChars(&IsWcsICmpLEquals, NULL, 0);
    printf("IsWcsNICmpLEquals");
    ScanIgnorableChars(&IsWcsNICmpLEquals, NULL, 0);
    printf("IsWmemCmpEquals");
    ScanIgnorableChars(&IsWmemCmpEquals, NULL, 0);
    printf("IsLStrcmpWEquals");
    ScanIgnorableChars(&IsLStrcmpWEquals, NULL, 0);
    printf("IsLStrcmpIWEquals");
    ScanIgnorableChars(&IsLStrcmpIWEquals, NULL, 0);
    printf("IsStrcmpEquals");
    ScanIgnorableChars(&IsStrcmpEquals, NULL, 0);
    printf("IsStrncmpEquals");
    ScanIgnorableChars(&IsStrncmpEquals, NULL, 0);
    printf("IsStrCollEquals");
    ScanIgnorableChars(&IsStrCollEquals, NULL, 0);
    printf("IsStrIcmpEquals");
    ScanIgnorableChars(&IsStrIcmpEquals, NULL, 0);
    printf("IsStrNIcmpEquals");
    ScanIgnorableChars(&IsStrNIcmpEquals, NULL, 0);
    printf("IsStrIcmplEquals");
    ScanIgnorableChars(&IsStrIcmplEquals, NULL, 0);
    printf("IsStrNIcmplEquals");
    ScanIgnorableChars(&IsStrNIcmplEquals, NULL, 0);
    printf("IsMemiCmpEquals");
    ScanIgnorableChars(&IsMemiCmpEquals, NULL, 0);
    printf("IsMemiCmpLEquals");
    ScanIgnorableChars(&IsMemiCmpLEquals, NULL, 0);
    printf("IslstrcmpAEquals");
    ScanIgnorableChars(&IslstrcmpAEquals, NULL, 0);
    printf("IslstrcmpiAEquals");
    ScanIgnorableChars(&IslstrcmpiAEquals, NULL, 0);
    printf("IsCompareStringOEquals");
    ScanIgnorableChars(&IsCompareStringOEquals, NULL, 0);
    printf("IsCompareStringAEquals");
    ScanIgnorableChars(&IsCompareStringAEquals, NULL, 0);
    printf("IsCompareStringWEquals");
    ScanIgnorableChars(&IsCompareStringWEquals, NULL, 0);
    printf("IsCompareStringExEquals");
    ScanIgnorableChars(&IsCompareStringExEquals, NULL, 0);

    printf("IsWcsCmpEquals");
    ScanCharEquals1(&IsWcsCmpEquals);
    printf("IsWcsNCmpEquals");
    ScanCharEquals1(&IsWcsNCmpEquals);
    printf("IsWcsCollEquals");
    ScanCharEquals1(&IsWcsCollEquals);
    printf("IsWcsIcmpEquals");
    ScanCharEquals1(&IsWcsIcmpEquals);
    printf("IsWcsNICmpEquals");
    ScanCharEquals1(&IsWcsNICmpEquals);
    printf("IsWcsICmpLEquals");
    ScanCharEquals1(&IsWcsICmpLEquals);
    printf("IsWcsNICmpLEquals");
    ScanCharEquals1(&IsWcsNICmpLEquals);
    printf("IsWmemCmpEquals");
    ScanCharEquals1(&IsWmemCmpEquals);
    printf("IsLStrcmpWEquals");
    ScanCharEquals1(&IsLStrcmpWEquals);
    printf("IsLStrcmpIWEquals");
    ScanCharEquals1(&IsLStrcmpIWEquals);
    printf("IsStrcmpEquals");
    ScanCharEquals1(&IsStrcmpEquals);
    printf("IsStrncmpEquals");
    ScanCharEquals1(&IsStrncmpEquals);
    printf("IsStrCollEquals");
    ScanCharEquals1(&IsStrCollEquals);
    printf("IsStrIcmpEquals");
    ScanCharEquals1(&IsStrIcmpEquals);
    printf("IsStrNIcmpEquals");
    ScanCharEquals1(&IsStrNIcmpEquals);
    printf("IsStrIcmplEquals");
    ScanCharEquals1(&IsStrIcmplEquals);
    printf("IsStrNIcmplEquals");
    ScanCharEquals1(&IsStrNIcmplEquals);
    printf("IsMemiCmpEquals");
    ScanCharEquals1(&IsMemiCmpEquals);
    printf("IsMemiCmpLEquals");
    ScanCharEquals1(&IsMemiCmpLEquals);
    printf("IslstrcmpAEquals");
    ScanCharEquals1(&IslstrcmpAEquals);
    printf("IslstrcmpiAEquals");
    ScanCharEquals1(&IslstrcmpiAEquals);
    printf("IsCompareStringOEquals");
    ScanCharEquals1(&IsCompareStringOEquals);
    printf("IsCompareStringAEquals");
    ScanCharEquals1(&IsCompareStringAEquals);
    printf("IsCompareStringWEquals");
    ScanCharEquals1(&IsCompareStringWEquals);
    printf("IsCompareStringExEquals");
    ScanCharEquals1(&IsCompareStringExEquals);
}

int main(int argc, const char * argv[])
{
    while (true)
    {
        int option = 0;

        printf("\n");
        printf("Unicode Cross-Locale Equivalence Analysis Tool\n\n");
        printf("Please choose an item:\n");
        printf("1. Show validation result of CompareStringW() with modifier letter arguments.\n");
        printf("11. Show result of all comparsion function.\n");
        printf("2. Show validation result of IdnToAscii() with modifier letter arguments.\n");
        printf("3. Run CompareStringW parameter space scanning.\n");
        printf("4. Run CompareStringW character scanning(signle character equal-comparsion).\n");
        printf("51. Run program scanning for CompareStringW.\n");
        printf("52. Run program scanning for CompareStringEx.\n");
        printf("53. Run program scanning for CompareStringA.\n");
        printf("60. Scan ignorable characters for arbitrary insertion.\n");
        printf("61. Scan single characters equal to a~z.\n");
        printf("62. Scan 2-combination characters to a~z.\n");
        printf("63. Scan 3-combination characters to a~z.\n");
        printf("64. Scan 4-combination characters to a~z.\n");
		printf("7. Test equivalence on all comparsion functions.\n");
        printf("9. Exit.\n");

        scanf("%d", &option);

        switch (option)
        {
        case 1:
            CompareStringSimpleTest();
            break;
        case 11:
            CompareStringAllComplexTest();
            break;
        case 2:
            IdnToAsciiSimpleTest();
            break;
        case 3:
            CompareStringWParameterTest();
            break;
        case 4:
            SingleWideCharComparsionTest();
            break;
        case 51:
            SystemProgramScanning("CompareStringW");
            break;
        case 52:
            SystemProgramScanning("CompareStringEx");
            break;
        case 53:
            SystemProgramScanning("CompareStringA");
            break;
        case 60:
            ScanIgnorableChars(&IsCompareStringWEquals, NULL, 0);
            break;
        case 61:
            ScanCharEquals1(&IsCompareStringWEquals);
            break;
        case 62:
            ScanCharEquals2(&IsCompareStringWEquals);
            break;
        case 7:
            FullStringComparsionValidation();
            break;
        case 9:
            return 0;
        }
    }

    return 0;
}