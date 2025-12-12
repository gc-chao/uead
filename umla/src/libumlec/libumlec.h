// All files within this DLL are compiled with the LIBUMLEC_EXPORTS
// symbol defined on the command line. This symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// LIBUMLEC_API functions as being imported from a DLL, whereas this DLL sees symbols
// defined with this macro as being exported.

#ifdef LIBUMLEC_EXPORTS
#define LIBUMLEC_API __declspec(dllexport)
#else
#define LIBUMLEC_API __declspec(dllimport)
#endif

LIBUMLEC_API bool IsModifierLetterExistInString(LPWSTR lpStr, int nLength);
LIBUMLEC_API int ConvertModifierLettersInString(LPWSTR lpStr, int nLength);