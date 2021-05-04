#define CURL_STATICLIB

#include <iostream>
#include <curl\curl.h>
#include <fstream>
#include <string>

static size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp)
{
	((std::string*)userp)->append((char*)contents, size * nmemb);
	return size * nmemb;
}

int main(int argc, char **argv) {
	std::ifstream versionfile("version.txt");
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
			curl_easy_perform(curl);
			curl_easy_cleanup(curl);

			int latestVersion = stoi(readBuffer);
			if (isNewerVersion = latestVersion > version) {
				std::cout << "There is a newer version! " << latestVersion << std::endl;
			}
		}
		else {
			std::cerr << "CURL error" << std::endl;
		}
	}

	versionfile.close();

	std::string command = "javaw -jar fileviewer.jar ";
	for (int i = 1; i < argc; i++) {
		command += argv[i];
		command += " ";
	}
	command += isNewerVersion ? "true" : "false";
	std::cout << "Starting command: " << command << std::endl;
	system(command.c_str());

	return 0;
}