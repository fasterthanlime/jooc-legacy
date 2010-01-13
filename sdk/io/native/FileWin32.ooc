
import structs/ArrayList
import ../File

//INVALID_HANDLE_VALUE: extern Handle
//FILE_ATTRIBUTE_DIRECTORY: extern Long // DWORD

version(windows) {
	
	// includes
	include windows
	
	// separators
	File separator = '\\'
    File pathDelimiter = ';'
    
    // covers
	Handle: cover from HANDLE
	
	INVALID_HANDLE_VALUE: extern Handle
	
    FindData: cover from WIN32_FIND_DATA {
		attr: extern(dwFileAttributes) Long // DWORD
	}
	FILE_ATTRIBUTE_DIRECTORY: extern Long // DWORD
	
	// functions from Win32
	FindFirstFile: extern func (String, FindData*) -> Handle
	FindClose: extern func (Handle)

	_remove: func(path: String) -> Int {
		printf("Win32: should remove file %s\n", path)
	}

	FileWin32: class extends File {

		init: func ~win32 (=path) {}

		findSingle: func (ffdPtr: FindData*) {
			hFind := findFirst(ffdPtr)
			FindClose(hFind)
		}

		findFirst: func (ffdPtr: FindData*) -> Handle {
			hFind := FindFirstFile(path, ffdPtr)
			if(hFind == INVALID_HANDLE_VALUE) {
				Exception new("Got invalid handle for file %s" format(path)) throw()
			}
			return hFind
		}

		isDir: func -> Bool {
			ffd: FindData
			findSingle(ffd&)
			return ((ffd attr) & FILE_ATTRIBUTE_DIRECTORY)
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
