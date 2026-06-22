package it.polimi.ingsw.am43.network.rmi;

import it.polimi.ingsw.am43.network.VirtualClient;

import java.rmi.Remote;

/**
 * RMI-specific marker interface for a client endpoint. It adds nothing to
 * {@link VirtualClient}; it exists only to give the RMI runtime a concrete
 * {@link Remote} type to export and look up as the client's stub.
 */
public interface VirtualClientRmi extends VirtualClient, Remote {
}
