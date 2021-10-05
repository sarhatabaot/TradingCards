package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class UuidUtil {
	public static UUID nil = UUID.fromString("00000000-0000-0000-0000-000000000000");
	public static @NotNull UUID getPlayerUuid(final @NotNull String name) {
		Gson gson = new Gson();
		URL playerdbApi;
		URLConnection urlConnection = null;
		try {
			playerdbApi = new URL("https://playerdb.co/api/player/minecraft/"+name.trim());
			urlConnection = playerdbApi.openConnection();
			urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
		} catch (IOException e){
			e.printStackTrace();
		}
		try (InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream())){
			return UUID.fromString(gson.fromJson(reader, JsonObject.class).getAsJsonObject("data").getAsJsonObject("player").get("id").getAsString());
		} catch (NullPointerException | IOException e){
			e.printStackTrace();
		}

		return Bukkit.getOfflinePlayer(name).getUniqueId();
	}
}
