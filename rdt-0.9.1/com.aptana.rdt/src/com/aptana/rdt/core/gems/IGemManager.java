package com.aptana.rdt.core.gems;

import java.util.Set;

import org.eclipse.core.runtime.IPath;

public interface IGemManager {

	/**
	 * Returns a listing of the local gems.
	 * 
	 * @return a Set<Gem> of the installed gems.
	 */
	public Set<Gem> getGems();

	/**
	 * Returns a listing of the remote gems.
	 * 
	 * @return a Set<Gem> of the remote gems available for install.
	 */
	public Set<Gem> getRemoteGems();

	/**
	 * Install the supplied gem including it's dependencies
	 * 
	 * @param gem
	 * @return
	 */
	public boolean installGem(Gem gem);

	/**
	 * Install teh supplied gem
	 * 
	 * @param gem
	 * @param includeDependencies
	 * @return
	 */
	public boolean installGem(Gem gem, boolean includeDependencies);

	/**
	 * Removes or uninstalls the supplied gem.
	 * 
	 * @param gem
	 *            the Gem to remove
	 * @return a boolean indicating whether the operation was a success.
	 */
	public boolean removeGem(Gem gem);

	/**
	 * Updates the supplied gem, if a newer version is available.
	 * 
	 * @param gem
	 *            the Gem to update
	 * @return a boolean indicating whether the operation was a success.
	 */
	public boolean update(Gem gem);

	/**
	 * Runs a "gem update" command for all gems. For any local gems that have a
	 * newer version, the IOGemManager will install the new versions.
	 * 
	 * @return a boolean indicating whether the operation was a success.
	 */
	public boolean updateAll();

	/**
	 * Query method to indicate if a gem using the supplied name is installed
	 * locally.
	 * 
	 * @param gemName
	 *            The unique name of the gem
	 * @return a boolean indicating whether the gem is installed
	 */
	public boolean gemInstalled(String gemName);

	/**
	 * Forces the GemManager to refresh it's listing of local gems
	 * 
	 * @return a boolean indicating if it was a successful operation
	 */
	public boolean refresh();

	public void addGemListener(GemListener listener);

	public void removeGemListener(GemListener listener);

	/**
	 * The base IPath where gems are installed.
	 * 
	 * @return an IPath indicating where gems get installed
	 */
	public IPath getGemInstallPath();

	/**
	 * Returns an IPath pointing to the latest version of the gem with the
	 * supplied name.
	 * 
	 * @param gemName
	 * @return an IPath pointing to the gem's location on disk for the latest
	 *         version of the gem
	 */
	public IPath getGemPath(String gemName);

	/**
	 * Returns an IPath pointing to the supplied version of the gem with the
	 * supplied name.
	 * 
	 * @param gemName
	 *            The name of the gem to match
	 * @param version
	 *            The version to match
	 * @return an IPath pointing to the gem's location on disk
	 */
	public IPath getGemPath(String gemName, String version);

	/**
	 * Determine whether it appears that the RubyGems library is installed (for
	 * the current/default VM).
	 * 
	 * @return a boolean indicating if it appears that RubyGems is installed
	 */
	public boolean isRubyGemsInstalled();

	/**
	 * Forces the IGemManager to load local cached copies of the remote and
	 * local gems listings. If empty, it refreshes it's local and remote gem
	 * listing from original sources.
	 */
	public void initialize();

	/**
	 * Query to indicate whether the IGemManager has been initialized yet.
	 * 
	 * @return
	 */
	public boolean isInitialized();

}