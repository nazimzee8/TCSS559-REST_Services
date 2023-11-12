package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/file")
public class FileUpload {
	
    @Path("upload")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response POSTHandler(@FormDataParam("file") InputStream uploadedInputStream,
    		@FormParam("file") FormDataContentDisposition fileDetail) throws IOException {
        // gets absolute path of the web application
        //String applicationPath = request.getServletContext().getRealPath("");
		String applicationPath = Assign4.absolutePath;
        // constructs path of the directory to save uploaded file
        //uploadFilePath.append(applicationPath + File.separator + UPLOAD_DIR);
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(applicationPath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdirs();
        }
        System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());
        String fileName = fileDetail.getFileName();
        applicationPath += File.separator + fileName;
        this.writeToFile(uploadedInputStream, applicationPath);
        //file = new File(absolutePath.toString());
        String output = "File successfully uploaded to : " + applicationPath;  
        return Response.status(200).entity(output).build();  
    } 
    
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {
			 
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			 
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
