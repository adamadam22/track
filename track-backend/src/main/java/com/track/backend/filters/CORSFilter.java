package com.track.backend.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/*filter add header to every Response. It let us to JavaSript from another host
 * could use API. By default modern browser block access to server when host has
 * another origin.
 */
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public ContainerResponse filter(ContainerRequest request,
            ContainerResponse response) {

        response.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHttpHeaders().add("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, Authorization, username, password, jsonWebToken, timestamp, route, friend,friendUsername");
        response.getHttpHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHttpHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS, HEAD");

        return response;
    }
}