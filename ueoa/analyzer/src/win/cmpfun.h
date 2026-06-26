
#include <Windows.h>
#include <locale.h>
#include <wchar.h>

typedef bool(*pfnIsEqual)(LPWSTR, LPWSTR);

bool IsWcsCmpEquals(LPWSTR, LPWSTR);
bool IsWcsNCmpEquals(LPWSTR, LPWSTR);
bool IsWcsCollEquals(LPWSTR, LPWSTR);
bool IsWcsIcmpEquals(LPWSTR, LPWSTR);
bool IsWcsNICmpEquals(LPWSTR, LPWSTR);
bool IsWcsICmpLEquals(LPWSTR, LPWSTR);
bool IsWcsNICmpLEquals(LPWSTR, LPWSTR);
bool IsWmemCmpEquals(LPWSTR, LPWSTR);
bool IsLStrcmpWEquals(LPWSTR, LPWSTR);
bool IsLStrcmpIWEquals(LPWSTR, LPWSTR);
bool IsCompareStringOEquals(LPWSTR, LPWSTR);
bool IsCompareStringAEquals(LPWSTR, LPWSTR);
bool IsCompareStringWEquals(LPWSTR, LPWSTR);
bool IsCompareStringExEquals(LPWSTR, LPWSTR);
bool IsStrcmpEquals(LPWSTR, LPWSTR);
bool IsStrncmpEquals(LPWSTR, LPWSTR);
bool IsStrCollEquals(LPWSTR, LPWSTR);
bool IsStrIcmpEquals(LPWSTR, LPWSTR);
bool IsStrNIcmpEquals(LPWSTR, LPWSTR);
bool IsStrIcmplEquals(LPWSTR, LPWSTR);
bool IsStrNIcmplEquals(LPWSTR, LPWSTR);
bool IsMemiCmpEquals(LPWSTR, LPWSTR);
bool IsMemiCmpLEquals(LPWSTR, LPWSTR);
bool IslstrcmpAEquals(LPWSTR, LPWSTR);
bool IslstrcmpiAEquals(LPWSTR, LPWSTR);