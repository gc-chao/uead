
#include "cmpfun.h"

bool IsWcsCmpEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == wcscmp(str1, str2);
}

bool IsWcsNCmpEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == wcsncmp(str1, str2, 255);
}

bool IsWcsCollEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == wcscoll(str1, str2);
}

bool IsWcsIcmpEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == _wcsicmp(str1, str2);
}

bool IsWcsNICmpEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == wcsnicmp(str1, str2, 255);
}

bool IsWcsICmpLEquals(LPWSTR str1, LPWSTR str2)
{
	_locale_t locale = _create_locale(LC_ALL, "en-US.UTF-8");

	bool value = 0 == _wcsicmp_l(str1, str2, locale);

	_free_locale(locale);

	return value;
}

bool IsWcsNICmpLEquals(LPWSTR str1, LPWSTR str2)
{
	_locale_t locale = _create_locale(LC_ALL, "en-US.UTF-8");

	bool value = 0 == _wcsnicmp_l(str1, str2, 255, locale);

	_free_locale(locale);

	return value;
}

bool IsWmemCmpEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == wmemcmp(str1, str2, max(wcslen(str1), wcslen(str2)));
}

bool IsLStrcmpWEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == lstrcmpW(str1, str2);
}

bool IsLStrcmpIWEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == lstrcmpiW(str1, str2);
}

bool IsCompareStringOEquals(LPWSTR str1, LPWSTR str2)
{
	return 0 == CompareStringOrdinal(str1, -1, str2, -1, FALSE) || 0 == CompareStringOrdinal(str1, -1, str2, -1, TRUE);
}

bool IsCompareStringAEquals(LPWSTR str1, LPWSTR str2)
{
	return CSTR_EQUAL == CompareStringA(LOCALE_INVARIANT, NORM_IGNORECASE, (char*)str1, -1, (char*)str2, -1);
}

bool IsCompareStringWEquals(LPWSTR str1, LPWSTR str2)
{
	return CSTR_EQUAL == CompareStringW(LOCALE_INVARIANT, NORM_IGNORECASE, str1, -1, str2, -1);
}

bool IsCompareStringExEquals(LPWSTR str1, LPWSTR str2)
{
	return CSTR_EQUAL == CompareStringEx(NULL, NORM_IGNORECASE, str1, -1, str2, -1, NULL, NULL, NULL);
}

bool IsStrcmpEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == strcmp(buf1, buf2);
}

bool IsStrncmpEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == strncmp(buf1, buf2, 255);
}

bool IsStrCollEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == strcoll(buf1, buf2);
}

bool IsStrIcmpEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == stricmp(buf1, buf2);
}

bool IsStrNIcmpEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == strnicmp(buf1, buf2, 255);
}

bool IsStrIcmplEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	_locale_t locale = _create_locale(LC_ALL, "en-US.UTF-8");

	bool value = 0 == _stricmp_l(buf1, buf2, locale);

	_free_locale(locale);

	return value;
}

bool IsStrNIcmplEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	_locale_t locale = _create_locale(LC_ALL, "en-US.UTF-8");

	bool value = 0 == _strnicmp_l(buf1, buf2, 255, locale);

	_free_locale(locale);

	return value;
}

bool IsMemiCmpEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == memicmp(buf1, buf2, max(strlen(buf1), strlen(buf2)));
}

bool IsMemiCmpLEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	_locale_t locale = _create_locale(LC_ALL, "en-US.UTF-8");

	bool value = 0 == _memicmp_l(buf1, buf2, 255, locale);

	_free_locale(locale);

	return value;
}

bool IslstrcmpAEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == lstrcmpA(buf1, buf2);
}

bool IslstrcmpiAEquals(LPWSTR str1, LPWSTR str2)
{
	char buf1[255] = { 0 };
	char buf2[255] = { 0 };

	WideCharToMultiByte(CP_UTF8, 0, str1, -1, buf1, 255, NULL, NULL);
	WideCharToMultiByte(CP_UTF8, 0, str2, -1, buf2, 255, NULL, NULL);

	return 0 == lstrcmpiA(buf1, buf2);
}