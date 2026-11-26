package service;

import communication.Request;
import communication.Response;

public interface Service {
    Response handleRequest(Request request);
}

// may want to remove unless more shared functionality discovered for 
// general service class