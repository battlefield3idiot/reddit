package com.delete.unwanted.messages;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.CommentReference;
import net.dean.jraw.references.PublicContributionReference;

@SpringBootApplication
public class Library implements CommandLineRunner {
	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(Library.class, args), () -> 0));
	}

	@Override
	public void run(String... args) throws Exception {
		
		UserAgent userAgent = new UserAgent("bot", "com.delete.unwanted.messages", "v0.1", "username");
		Credentials credentials = Credentials.script("username", "password", "code1",
				"code2");

		// This is what really sends HTTP requests
		NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);

		// Authenticate and get a RedditClient instance
		RedditClient reddit = OAuthHelper.automatic(adapter, credentials);

		DefaultPaginator<PublicContribution<?>> overview = reddit.me().history("overview").build();
		List<Listing<PublicContribution<?>>> list = new ArrayList<>();
		for (Listing<PublicContribution<?>> listing : overview) {
			list.add(listing);
		}

		List<PublicContribution<?>> contributions = new ArrayList<>();
		for (Listing<PublicContribution<?>> item : list) {
			int size = item.size();
			for (int i = 0; i < size; i++) {
				PublicContribution<?> pc = item.get(i);
				contributions.add(pc);
			}
		}

		List<String> ids = new ArrayList<>();
		contributions.forEach(c -> {
			ids.add(c.getFullName());
		});
		
		for (String id : ids) {
			reddit.publicContribution(id).delete();
		}
	}
}
