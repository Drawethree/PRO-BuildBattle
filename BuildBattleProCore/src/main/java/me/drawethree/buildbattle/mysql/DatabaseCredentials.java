package me.drawethree.buildbattle.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseCredentials {
	private final String host, databaseName, userName, password;
	private final int port;
}
