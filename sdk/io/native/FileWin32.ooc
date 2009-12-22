
import structs/ArrayList
import ../File

version(windows) {
	
	// includes
	include windows
	
	// separators
	File separator = '\\'
    File pathDelimiter = ';'
    
    // covers
    FindData: cover from WIN32_FIND_DATA {
		
	}

	_remove: func(path: String) -> Int {
		printf("Win32: should remove file %s\n", path)
	}

	FileWin32: class extends File {

		init: func ~win32 (=path) {}

		isDir: func -> Bool {
			// FIXME stub
			return false
		}
		
		isFile: func -> Bool {
			// FIXME stub
			return false
		}
		
		isLink: func -> Bool {
			// FIXME stub
			return false
		}
		
		size: func -> Int {
			// FIXME stub
			return 0
		}
		
		/**
		 * @return the permissions for the owner of this file
		 */
		ownerPerm: func -> Int {
			// FIXME stub
			return 0
		}
		
		/**
		 * @return the permissions for the group of this file
		 */
		groupPerm: func -> Int {
			// FIXME stub
			return 0
		}
		
		/**
		 * @return the permissions for the others (not owner, not group)
		 */
		otherPerm: func -> Int {
			// FIXME stub
			return 0
		}
		
		mkdir: func ~withMode (mode: Int32) -> Int {
			// FIXME stub
			return -1
		}

		/**
		 * @return the time of last access
		 */
		lastAccessed: func -> Long {
			// FIXME stub
			return 0
		}
		
		/**
		 * @return the time of last modification
		 */
		lastModified: func -> Long {
			// FIXME stub
			return 0
		}
		
		/**
		 * @return the time of creation
		 */
		created: func -> Long {
			// FIXME stub
			return 0
		}
		
		/**
		 * The absolute path, e.g. "my/dir" => "/current/directory/my/dir"
		 */
		getAbsolutePath: func -> String {
			// FIXME stub
			return ""
		}
		
		/**
		 * A file corresponding to the absolute path
		 * @see getAbsolutePath
		 */
		getAbsoluteFile: func -> String {
			// FIXME stub
			return null
		}
		
		/**
		 * List the name of the children of this path
		 * Works only on directories, obviously
		 */
		getChildrenNames: func -> ArrayList<String> {
			// FIXME stub
			return ArrayList<String> new()
		}
		
		/**
		 * List the children of this path
		 * Works only on directories, obviously
		 */
		getChildren: func -> ArrayList<This> {
			// FIXME stub
			return ArrayList<This> new()
		}

	}

}
