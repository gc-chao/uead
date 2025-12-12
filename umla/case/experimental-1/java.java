import java.util.regex.Pattern;

public class ModifierComparsion
{
    public static void main(String[] args)
    {
        String[] engLetters = new String[] { "a", "e", "i", "o", "u", "x", "l", "w", "r" };
        String[] modLetters = new String[] { "ᵃ", "ᵉ", "ᴵ", "ᴼ", "ᵁ", "ˣ", "ˡ", "ʷ", "ʳ" };

        for (int i = 0; i < engLetters.length; i++)
        {
            if (engLetters[i].equals(modLetters[i]) || modLetters[i].equals(engLetters[i]))
            {
                String info = String.format("Engligh letter '%c' equals with modifier letter '%c'.", engLetters[i], modLetters[i]);

                System.out.println(info);
            }
        }

        for (int i = 0; i < engLetters.length; i++)
        {
            boolean result1 = Pattern.matches(modLetters[i], engLetters[i]);
            boolean result2 = Pattern.matches(engLetters[i], modLetters[i]);

            if (result1 || result2)
            {
                String info = String.format("Engligh string '%s' equals with modifier string '%s'.", engLetters[i], modLetters[i]);

                System.out.println(info);
            }
        }

        System.out.println("Test completed.");
    }
}