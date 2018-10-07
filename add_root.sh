#!/bin/bash
for file in ./dataset/*; do
	if [ "$file" != "./add_root.sh" ]; then
		echo '<ROOT>' > tmp
		cat "$file" >> tmp
		echo '</ROOT>' >> tmp
		cp tmp "$file"
		rm tmp
	fi
done
