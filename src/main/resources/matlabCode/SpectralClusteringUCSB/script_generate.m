function cluster_labels = script_generate(array, map_points, neighbors, num_clusters, sigma)
%  GEN_NN_DISTANCE Generate (t-nearest-neighbor) sparse distance matrix.
%  function gen_nn_distance(data, num_neighbors, block_size, save_type)
%
%   Input  : data         : N-by-D data matrix, where N is the number of data,
%                           D is the number of dimensions
%            num_neighbors: number of nearest neighbors
%            block_size   : block size for partitioning data matrix
%            save_type    : 0 for .mat file, 1 for .txt file, 2, for both
%
%   Output : Either .mat or .txt or both files

%
% Divide data into blocks, process each block sequentially to alleviate memory use
%
close all
plotchoices = {'bo','r+','md','k*','bv', 'b+', 'k+'};


img = imread('Indoor2.png');
 
% set the range of the axes
% The image will be stretched to this.
min_x = 0;
max_x = 500;
min_y = 0;
max_y = 500;

%%%%%%%%%%%%%Performaing K-means partioning%%%%%%%%%%
%cluster_labels = k_means(array, 'random' , num_clusters);

%figure;
%for i = 1: num_clusters
%    points = array(cluster_labels == i, :);
%    plot(points(:,1), points(:,2), plotchoices{i});
%hold on;
%end
%title('clustered data using k-means');
%grid on;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


figure('units','normalized','outerposition',[0 0 1 1])

subplot(1,2,1);
imagesc([min_x max_x], [min_y max_y], img);
hold on;

cluster_labels = k_means(array, 'random' , num_clusters);
for i = 1: num_clusters
    points = map_points(cluster_labels == i, :);
    plot(points(:,1), points(:,2), plotchoices{i});
    hold on;
end
    title(strcat({'K-means clustering with '},num2str(num_clusters), {' clusters, '},num2str(neighbors), ' neighbors' ));
grid on;

save_type = 0; % save file with '.mat' format
block_size = 50;
%sigma = 0; % keep sigma = 0 for self-tuning technique
gen_nn_distance(array, neighbors, block_size, save_type);
outfile = [num2str(neighbors), '_NN_sym_distance.mat'];
load(outfile);

% function [cluster_labels evd_time kmeans_time total_time] = sc(A, sigma, num_clusters)
% SC Spectral clustering using a sparse similarity matrix (t-nearest-neighbor).
%
%   Input  : A              : N-by-N sparse distance matrix, where
%                             N is the number of data
%            sigma          : sigma value used in computing similarity,
%                             if 0, apply self-tunning technique
%            num_clusters   : number of clusters
%
%   Output : cluster_labels : N-by-1 vector containing cluster labels
%            evd_time       : running time for eigendecomposition
%            kmeans_time    : running time for k-means
%            total_time     : total running time

%
% Convert the sparse distance matrix to a sparse similarity matrix,
% where S = exp^(-(A^2 / 2*sigma^2)).
% Note: This step can be ignored if A is sparse similarity matrix.
%
[cluster_labels evd_time kmeans_time total_time] = sc(A, sigma, num_clusters)

%figure;
%for i = 1: num_clusters
%    points = array(cluster_labels == i, :);
%    plot(points(:,1), points(:,2), plotchoices{i});
%    hold on;
%end
%title('clustered data using spectral clustering');
%grid on;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%figure;
subplot(1,2,2);
imagesc([min_x max_x], [min_y max_y], img);
hold on;
    for i = 1: num_clusters
        points = map_points(cluster_labels == i, :);
        plot(points(:,1), points(:,2), plotchoices{i});
    hold on;
    end
    title(strcat({'Spectral clustering with '},num2str(num_clusters), {' clusters, '},num2str(neighbors), ' neighbors' ));
    grid on;
