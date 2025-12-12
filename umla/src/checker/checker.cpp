
// checker.cpp : Defines the entry point for the application.

#include "stdafx.h"
#include "checker.h"

#define IDB_SHOW 100
#define IDB_CONVERT 101
#define IDC_EDIT_1 102
#define IDC_EDIT_2 103

#define MAX_LOADSTRING 100

// Global Variables:
HINSTANCE hInst;								// current instance
TCHAR szTitle[MAX_LOADSTRING];					// The title bar text
TCHAR szWindowClass[MAX_LOADSTRING];			// the main window class name

// Forward declarations of functions included in this code module:
LRESULT CALLBACK WndProc(HWND, UINT, WPARAM, LPARAM);
INT_PTR CALLBACK About(HWND, UINT, WPARAM, LPARAM);

void ShowModifiers(HWND hWnd);

int APIENTRY _tWinMain(_In_ HINSTANCE hInstance,
    _In_opt_ HINSTANCE hPrevInstance,
    _In_ LPTSTR    lpCmdLine,
    _In_ int       nCmdShow)
{
    hInst = hInstance; // Store instance handle in our global variable

    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);

    // Initialize global strings
    LoadString(hInstance, IDS_APP_TITLE, szTitle, MAX_LOADSTRING);
    LoadString(hInstance, IDC_CHECKER, szWindowClass, MAX_LOADSTRING);

    WNDCLASSEX wcex;

    wcex.cbSize = sizeof(WNDCLASSEX);
    wcex.style = CS_HREDRAW | CS_VREDRAW;
    wcex.lpfnWndProc = WndProc;
    wcex.cbClsExtra = 0;
    wcex.cbWndExtra = 0;
    wcex.hInstance = hInstance;
    wcex.hIcon = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_CHECKER));
    wcex.hCursor = LoadCursor(NULL, IDC_ARROW);
    wcex.hbrBackground = (HBRUSH)(COLOR_WINDOW + 1);
    wcex.lpszMenuName = MAKEINTRESOURCE(IDC_CHECKER);
    wcex.lpszClassName = szWindowClass;
    wcex.hIconSm = LoadIcon(wcex.hInstance, MAKEINTRESOURCE(IDI_SMALL));

    RegisterClassEx(&wcex);

    HWND hWnd = CreateWindow(szWindowClass, szTitle, WS_DLGFRAME | WS_SYSMENU,
        CW_USEDEFAULT, 0, 400, 210, NULL, NULL, hInstance, NULL);

    ShowWindow(hWnd, nCmdShow);
    UpdateWindow(hWnd);

    HACCEL hAccelTable = LoadAccelerators(hInstance, MAKEINTRESOURCE(IDC_CHECKER));

    MSG msg;

    // Main message loop:
    while (GetMessage(&msg, NULL, 0, 0))
    {
        if (!TranslateAccelerator(msg.hwnd, hAccelTable, &msg))
        {
            TranslateMessage(&msg);
            DispatchMessage(&msg);
        }
    }

    return (int)msg.wParam;
}

// FUNCTION: WndProc(HWND, UINT, WPARAM, LPARAM)
LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam)
{
    int wmId, wmEvent;
    PAINTSTRUCT ps;
    HDC hdc;

    switch (message)
    {
    case WM_CREATE:
    {
        CreateWindow(WC_BUTTON, L"Show Modifiers", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            20, 100, 150, 28, hWnd, (HMENU)IDB_SHOW, NULL, NULL);

        CreateWindow(WC_BUTTON, L"Convert", WS_CHILD | WS_VISIBLE | BS_PUSHBUTTON,
            200, 100, 150, 28, hWnd, (HMENU)IDB_CONVERT, NULL, NULL);

        CreateWindow(WC_EDIT, L"ᴴᵉˡˡᵒʷᴼʳˡᴰ!", WS_CHILDWINDOW | WS_BORDER | WS_VISIBLE | ES_AUTOHSCROLL,
            10, 10, 350, 25, hWnd, (HMENU)IDC_EDIT_1, NULL, NULL);
        
        CreateWindow(WC_EDIT, L"", WS_CHILDWINDOW | WS_BORDER | WS_VISIBLE | ES_AUTOHSCROLL,
            10, 40, 350, 25, hWnd, (HMENU)IDC_EDIT_2, NULL, NULL);
    }

    case WM_COMMAND:

        wmId = LOWORD(wParam);
        wmEvent = HIWORD(wParam);

        // Parse the menu selections:
        switch (wmId)
        {
        case IDB_SHOW:

            if (wmEvent == BN_CLICKED)
                ShowModifiers(hWnd);

            break;

        case IDB_CONVERT:

            if (wmEvent == BN_CLICKED)
            {
                HWND hEdit = GetDlgItem(hWnd, IDC_EDIT_1);
                HWND hResult = GetDlgItem(hWnd, IDC_EDIT_2);
                WCHAR buffer[MAX_LOADSTRING] = { 0 };
                GetWindowText(hEdit, buffer, MAX_LOADSTRING);
                ConvertModifierLettersInString(buffer, -1);
                SetWindowText(hResult, buffer);
            }

            break;

        case IDM_ABOUT:

            DialogBox(hInst, MAKEINTRESOURCE(IDD_ABOUTBOX), hWnd, About);

            break;

        case IDM_EXIT:

            DestroyWindow(hWnd);

            break;

        default:

            return DefWindowProc(hWnd, message, wParam, lParam);
        }

        break;

    case WM_PAINT:

        hdc = BeginPaint(hWnd, &ps);

        EndPaint(hWnd, &ps);

        break;

    case WM_DESTROY:

        PostQuitMessage(0);

        break;

    default:

        return DefWindowProc(hWnd, message, wParam, lParam);
    }

    return 0;
}

// Message handler for about box.
INT_PTR CALLBACK About(HWND hDlg, UINT message, WPARAM wParam, LPARAM lParam)
{
    UNREFERENCED_PARAMETER(lParam);

    switch (message)
    {
    case WM_INITDIALOG:

        return (INT_PTR)TRUE;

    case WM_COMMAND:

        if (LOWORD(wParam) == IDOK || LOWORD(wParam) == IDCANCEL)
        {
            EndDialog(hDlg, LOWORD(wParam));

            return (INT_PTR)TRUE;
        }

        break;
    }

    return (INT_PTR)FALSE;
}

void ShowModifiers(HWND hWnd)
{
    WCHAR buf1[2] = { 0 };
    WCHAR buf2[2] = { 0 };
    WCHAR output[1600] = { 0 };

    for (int i = 'a'; i <= 'z'; i++)
    {
        for (int j = 128; j < 65536; j++)
        {
            buf1[0] = i;
            buf2[0] = j;

            if (CSTR_EQUAL == CompareString(LOCALE_INVARIANT, NORM_IGNORECASE, buf1, -1, buf2, -2))
            {
                WCHAR buffer[16] = { 0 };

                wsprintf(buffer, L"%c\t0x%x\n", buf2[0], buf2[0]);
                wcscat_s(output, buffer);
            }
        }
    }

    MessageBox(hWnd, output, L"Modifiers", MB_OK);
}