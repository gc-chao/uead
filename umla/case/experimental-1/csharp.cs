
using System;
using System.Text.RegularExpressions;

namespace ModifierComparsion
{
    class Program
    {
        public static void Main()
        {
            String[] engLetters = new String[] { "a", "e", "i", "o", "u", "x", "l", "w", "r" };
            String[] modLetters = new String[] { "ᵃ", "ᵉ", "ᴵ", "ᴼ", "ᵁ", "ˣ", "ˡ", "ʷ", "ʳ" };

            for (int i = 0; i < engLetters.Length; i++)
            {
                if (engLetters[i].Equals(modLetters[i]) || modLetters[i].Equals(engLetters[i]))
                {
                    String info = String.Format("Engligh letter '{0}' equals with modifier letter '{1}'.", engLetters[i], modLetters[i]);
                
                    Console.WriteLine(info);
                }
            }

            for (int i = 0; i < engLetters.Length; i++)
            {
                bool result1 = Regex.IsMatch(modLetters[i], engLetters[i]);
                bool result2 = Regex.IsMatch(engLetters[i], modLetters[i]);

                if (result1 || result2)
                {
                    String info = String.Format("Engligh string '{0}' equals with modifier string '{1}'.", engLetters[i], modLetters[i]);

                    Console.WriteLine(info);
                }
            }

            Console.WriteLine("Test completed.");
        }
    }
}