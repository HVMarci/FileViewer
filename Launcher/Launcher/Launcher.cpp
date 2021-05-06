#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#define CURL_STATICLIB

#include <iostream>
#include <curl\curl.h>
#include <fstream>
#include <string>
#include <Windows.h>
#include <shellapi.h>

std::string GetCurrentDirectory()
{
	char buffer[MAX_PATH];
	GetModuleFileNameA(NULL, buffer, MAX_PATH);
	std::string::size_type pos = std::string(buffer).find_last_of("\\/");

	return std::string(buffer).substr(0, pos);
}

static size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp)
{
	((std::string*)userp)->append((char*)contents, size * nmemb);
	return size * nmemb;
}

int main(int argc, char **argv) {
	std::ifstream versionfile(GetCurrentDirectory() + "\\version.txt");
	int version;
	bool isNewerVersion = false;

	if (!(versionfile >> version)) {
		std::cerr << "Can't read the file" << std::endl;
	}
	else {
		std::cout << "Current version = " << version << std::endl;

		CURL* curl = curl_easy_init();
		std::string readBuffer;

		if (curl) {
			curl_easy_setopt(curl, CURLOPT_URL, "https://marci.hvj.hu/fileviewer/version.txt");
			curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
			curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);
			CURLcode res = curl_easy_perform(curl);
			if (res == CURLE_OK) {
				int latestVersion = stoi(readBuffer);
				if (isNewerVersion = latestVersion > version) {
					std::cout << "There is a newer version! " << latestVersion << std::endl;
				}
			}
			curl_easy_cleanup(curl);
		}
		else {
			std::cerr << "CURL error" << std::endl;
		}
	}

	versionfile.close();

	if (isNewerVersion) {
		int response = MessageBox(NULL, (LPCWSTR)L"Egy új verzió elérhetõ!\nSzeretné telepíteni?", (LPCWSTR)L"Frissítés", MB_YESNO | MB_ICONINFORMATION);
		if (response == IDYES) {
			ShellExecute(NULL, L"open", L"https://github.com/HVMarci/FileViewer/releases/latest", NULL, NULL, SW_SHOW);
		}
	}

	std::string command = "javaw -jar \"" + GetCurrentDirectory() + "\\fileviewer.jar\" \"";
	for (int i = 1; i < argc - 1; i++) {
		command += argv[i];
		command += " ";
	}
	command += argv[argc - 1];
	command += '"';
	std::cout << "Starting command: " << command << std::endl;
	system(command.c_str());

	return 0;
}