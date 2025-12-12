
#include <stdio.h>
#include <string.h>
#include <regex>
#include <iostream>

using namespace std;

#define ARRAY_SIZE 9

int main(void)
{
	wchar_t * engLetters[] = { L"a", L"e", L"i", L"o", L"u", L"x", L"l", L"w", L"r" };
	wchar_t * modLetters[] = { L"ᵃ", L"ᵉ", L"ᴵ", L"ᴼ", L"ᵁ", L"ˣ", L"ˡ", L"ʷ", L"ʳ" };

	for (int i = 0; i < ARRAY_SIZE; i++)
	{
		if (0 == wcscmp(engLetters[i], modLetters[i]))
		{
			cout << "Engligh letter '" << engLetters[i] << "' equals with modifier letter '" << modLetters[i] << "'." << endl;
		}
	}

	for (int i = 0; i < ARRAY_SIZE; i++)
	{
		wregex pattern1(modLetters[i]);
		wregex pattern2(engLetters[i]);

		bool result1 = regex_match(engLetters[i], pattern1);
		bool result2 = regex_match(modLetters[i], pattern2);

		if (result1 || result2)
		{
			cout << "Engligh string '" << modLetters[i] << "' equals with modifier string '" << engLetters[i] << "'." << endl;
		}
	}

	cout << "Test completed." << endl;

	return 0;
}