package main

import ("fmt"; "strings"; "regexp")

func main() {
    var engLetters = [...]string{ "a", "e", "i", "o", "u", "x", "l", "w", "r" }
    var modLetters = [...]string{ "ᵃ", "ᵉ", "ᴵ", "ᴼ", "ᵁ", "ˣ", "ˡ", "ʷ", "ʳ" }
    for i := 0; i < 9; i++ {
        if strings.Contains(modLetters[i], engLetters[i]) || strings.Contains(engLetters[i], modLetters[i]) {
            fmt.Println("Engligh letter '%s' equals with modifier letter '%s'.", engLetters[i], modLetters[i])
        }
    }
    for i := 0; i < 9; i++ {
        regexp1, err1 := regexp.Compile(modLetters[i]);
        regexp2, err2 := regexp.Compile(engLetters[i]);
        
        if err1 != nil || err2 != nil {
            fmt.Println("error")
        }
        
        if regexp1.Match([]byte(engLetters[i])) || regexp2.Match([]byte(modLetters[i])) {
            fmt.Println("Engligh string '%s' equals with modifier string '%s'.", engLetters[i], modLetters[i])
        }
    }
    fmt.Println("Test completed.");
}
