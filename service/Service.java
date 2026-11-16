package service;

import communication.Request;
import communication.Response;

public interface Service {
    Response handleRequest(Request request);
}
