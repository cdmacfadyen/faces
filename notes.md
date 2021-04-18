

## What do I need to know?
Have a unity diffuse coefficient, Lecture 9. 
Faces are Matte, lambert reflectance, Lecture 9 and 10.
Single directional light source aligned with viewing angle, Lecture 11.
Flat shading, Lecture 12.
Orthographic Projection, Lecture 13.
Painters algorithm, Lecture 14.

Do lectures 9 through 14.
crikey.


## Summary of csv file meanings
mesh.csv contains the indices for 
each triangle in the face. So the face 
always uses the same number of triangles 
and the coordinates of these 
triangles are found in the sh.csv files. The 
tx.csv files have the colours. 

Using processing again will be fine. 

How faces are represented.
The mesh gives you the triangles, 
each indexes coordinates of the mesh. You 
can't interpolate between two totally distinct 
things, but we have a standard face and then information 
about how each face deviates from this standard face. 
Then we can interpolate between the differences 
of each face from the standard face to generate the 
new face. 

I believe myself to be entirely understanding it now. 
Orthogonal projection literally just means totally ignore the z-coord. 
And you can do that because of these homogenous coords, 
but you don't actually need to do the calculations in homogenous coords.


## Questions

Are we supposed to submit all the data or can we just 
tell you where to put it?


## Basic Strat
start out with the triangle drawn and 
a face off to the side that is just the 
straight average of the three with no weights. 

For the faces, we draw them only using the x and y and then 
ignore the z, so we lose depth perception. But we can add in a
feeling of depth by using lighting and reflectance to 
figure out how the face reflects light.

Each vertex of the face has a colour, 
so we can average out the colour of the vertices and 
use that as the colour of the triangle, 
then we can draw the triangles and fill 
them with that colour. 
So the process is:
1. load face from file
2. turn face into series of triangles
3. order triangles according to how deep their centre is 
At this point we have the mesh ready to go.
4a. Draw all triangles with the colour taken from 
the average of the colour at each vertex.
4b. Do Lambert shading to update the colours.
5. Draw.

## Load face from file
Need to better understand how the csv files work.

200 tx and sh.csv files, one for each face. 
one sh_EV file
one tx_EV file
one mesh.csv file

Each line in mesh.csv indexes the 3D coordinates and colours 
in the sh and tx files. sh_000 and tx_000 are the average 
values, and then the actual face values 
are found by adding the values found in the 
offset files, which are all the other sh and tx files.

Each line in mesh.csv is one triangle. So each 
entry in a line is an index to look up in the 
sh_xxx file. Each line in the sh_xxx file 
is comma separated x,y,z offset, from the same 
line in the sh_000 file. Same for colour but with r,g,b.

To get the coordinate we multiply by the 
n-th weight in sh_EV.csv.

So for the first triangle in the first 
face, we open sh_000 and sh_001 and mesh
the first line in mesh has three integers for 
point1, point2 and point3 in the triangle. 
point1.x = sh_000[0][0] + (sh_001[0][0] * sh_EV[1])

point = average point + offset * weight

When we're interpolating we'll want to multiply the second 
term by the weight we've assigned it. 


Each face needs tx_xxx and sh_xxx.
Each index in mesh.csv is a line in 
sh_xxx and tx_xxx, which represents one point. 
So we can just load sh_xxx and tx_xxx into arrays 
and deal with them later.


## Shading
Question - Are we doing the shading calculation at the vertices 
and then averaging that colour across the face of the 
triangle?
Yes.

Since the viewpoint is infinitely far away 
the viewing direction vector is going to 
be (0,0,1). Could be (0,0,-1) lets check the lecture notes.   

Lectures have wi as (0,0,1) and intensity as 3.

## Engineering
So we have the face offsets and 
weights, but we also need the averages to 
do anything. 

So to get a face, we need to have an average face 
and an array of faces and an array of weights. 